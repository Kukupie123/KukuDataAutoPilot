import {KDAPLogger} from "../../../util/KDAPLogger";
import {ICusomtIndexAdapter} from "../../interface/ICusomtIndexAdapter";
import {combineStrings} from "./PouchHelper";
import {PouchDb} from "./PouchDb";

export class PouchDbIndexTable implements ICusomtIndexAdapter {
    private pouchDb: PouchDb;
    private logger: KDAPLogger;

    constructor(pouchDb: PouchDb) {
        this.pouchDb = pouchDb;
        this.logger = new KDAPLogger(PouchDbIndexTable.name);
    }

    async link(recordIDWorkspaceIDTuples: [string, string][]): Promise<string[]> {
        this.logger.log({
            msg: `Linking record&workspace : ${JSON.stringify(recordIDWorkspaceIDTuples)}`,
            func: this.link
        })
        for (const value of recordIDWorkspaceIDTuples) {
            const recID = value[0];
            const wsID = value[1];
            this.logger.log({msg: `Linking Record ${recID} to Workspace ${wsID}`, func: this.link});
            //TODO: Validate if rec and ws exists
            //Add the data to tables
            await this.pouchDb.wsRecIndexDB.put({_id: combineStrings(wsID, recID)})
            await this.pouchDb.recWsIndexDB.put({_id: combineStrings(recID, wsID)})
        }
        return Promise.resolve([]);
    }

    async getWorkspaceOfRecord(recordID: string): Promise<string[]> {
        //TODO:
    }

    async getRecordsOfWorkspace(workspaceID: string): Promise<string[]> {
        //TODO:
    }

}
