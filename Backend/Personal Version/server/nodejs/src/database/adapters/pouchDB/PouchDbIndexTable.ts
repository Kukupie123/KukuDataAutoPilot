import {KDAPLogger} from "../../../util/KDAPLogger";
import {ICusomtIndexAdapter} from "../../interface/ICusomtIndexAdapter";
import {breakStrings, combineStrings} from "./PouchHelper";
import {PouchDb} from "./PouchDb";
import {Category} from "../../../config/kdapLogger.config";

export class PouchDbIndexTable implements ICusomtIndexAdapter {
    private pouchDb: PouchDb;
    private logger: KDAPLogger;

    constructor(pouchDb: PouchDb) {
        this.pouchDb = pouchDb;
        this.logger = new KDAPLogger(PouchDbIndexTable.name);
    }

    async link(recordIDWorkspaceIDTuples: [string, string][]): Promise<[string, string][]> {
        this.logger.log({
            msg: `Linking record&workspace : ${JSON.stringify(recordIDWorkspaceIDTuples)}`,
            func: this.link
        })
        const failed: [string, string][] = [];
        for (const value of recordIDWorkspaceIDTuples) {
            const recID = value[0];
            const wsID = value[1];
            try {

                this.logger.log({msg: `Linking Record ${recID} to Workspace ${wsID}`, func: this.link});
                //TODO: Validate if rec and ws exists
                //Validate if rec exists
                await this.pouchDb.recordDB.get(recID);
                await this.pouchDb.workspaceDb.get(wsID);
                //Add the data to tables
                await this.pouchDb.wsRecIndexDB.put({_id: combineStrings(wsID, recID)})
                await this.pouchDb.recWsIndexDB.put({_id: combineStrings(recID, wsID)})
            } catch (err) {
                this.logger.log({
                    msg: `Failed to link workspace ${wsID} and record ${recID}`,
                    func: this.link,
                    category: Category.Error
                });
                failed.push([recID, wsID]);
            }
        }
        return failed;
    }

    async getWorkspacesOfRecord(recordID: string, skip: number, limit: number): Promise<string[]> {
        this.logger.log({
            msg: `Getting workspaces using record ${recordID}. Skipping : ${skip} and limit : ${limit}`,
            func: this.getWorkspacesOfRecord
        })
        const docs = await this.pouchDb.recWsIndexDB.allDocs(({skip: skip, limit: limit, include_docs: true}));
        const wss: string[] = [];
        docs.rows.forEach(item => {
            if (item.id.startsWith(recordID)) {
                const [rec, ws] = breakStrings(item.id);
                wss.push(ws);
            }
        })
        this.logger.log({msg: `Got workspaces ${JSON.stringify(wss)}`, func: this.getWorkspacesOfRecord});
        return wss;
    }

    async getRecordsOfWorkspace(workspaceID: string, skip: number, limit: number): Promise<string[]> {
        this.logger.log({
            msg: `Getting Records in workspace ${workspaceID}. Skipping : ${skip} and limit : ${limit}`,
            func: this.getRecordsOfWorkspace
        })
        const docs = await this.pouchDb.wsRecIndexDB.allDocs({skip: skip, limit: limit, include_docs: true});
        const recs: string[] = [];
        docs.rows.forEach(item => {
            if (item.id.startsWith(workspaceID)) {
                const [ws, rec] = breakStrings(item.id);
                recs.push(rec);
            }
        })
        this.logger.log({msg: `Got Records ${JSON.stringify(recs)}`, func: this.getRecordsOfWorkspace});
        return recs;
    }

    async deleteLink(workspaceID: string, recordID: string): Promise<boolean> {
        try {
            this.logger.log({
                msg: `Deleting link of workspace ${workspaceID} and recordID ${recordID}`,
                func: this.deleteLink
            })
            const wsRec = combineStrings(workspaceID, recordID);
            const recWs = combineStrings(recordID, workspaceID);
            const wsRecDoc = await this.pouchDb.wsRecIndexDB.get(wsRec);
            const recWsDoc = await this.pouchDb.recWsIndexDB.get(recWs);
            await this.pouchDb.wsRecIndexDB.remove({_id: wsRecDoc._id, _rev: wsRecDoc._rev});
            this.logger.log({
                msg: `Removed Workspace Rec Index DB Entry ${wsRec}`,
                func: this.deleteLink
            })
            await this.pouchDb.recWsIndexDB.remove({_id: recWsDoc._id, _rev: recWsDoc._rev});
            this.logger.log({
                msg: `Removed Rec Workspace Index DB Entry ${recWs}`,
                func: this.deleteLink
            })

            return true;
        } catch (err) {
            this.logger.log({
                msg: `Error when trying to delete link ${(err as Error).message}`,
                func: this.deleteLink
            })
            return false;
        }

    }

}
