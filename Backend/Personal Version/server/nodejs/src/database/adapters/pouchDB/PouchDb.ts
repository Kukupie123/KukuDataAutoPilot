import { IDatabaseAdapter } from "../../IDatabaseAdapter";
import { WorkspaceModel } from "../../../models/WorkspaceModel";
import databasePouch from "pouchdb";
import { KDAPLogger } from "../../../util/KDAPLogger";
import { Category } from "../../../config/kdapLogger.config";
import { RecordModel } from "../../../models/RecordModel";
import { workspaceDbDir, recordDbDir, generateProjectID } from "./PouchHelper";
import { validateAttribute } from "../../../util/RecordHelper";

export class PouchDb implements IDatabaseAdapter {
    private logger = new KDAPLogger(PouchDb.name);
    private workspaceDb!: PouchDB.Database;
    private recordDB!: PouchDB.Database;

    async init(): Promise<void> {
        this.logger.log({ msg: "Initializing Pouch", func: this.init });
        try {
            this.workspaceDb = new databasePouch(workspaceDbDir);
            this.recordDB = new databasePouch(recordDbDir);
            this.logger.log({ msg: "Initialized Pouch", func: this.init });

            this.logger.log({ msg: `Setting up listeners`, func: this.init });
        } catch (err: any) {
            this.logger.log({ msg: `Failed to initialize PouchDb: ${err.message}`, func: this.init });
            throw err;
        }
    }

    async dispose(): Promise<void> {
    }

    // Workspace Operations
    async addWorkspace(workspaceName: string, description?: string): Promise<WorkspaceModel> {
        this.logger.log({ msg: `Adding new workspace ${workspaceName}, ${description}`, func: this.addWorkspace });
        try {
            const ws: WorkspaceModel = new WorkspaceModel(workspaceName, description);

            /*
            Concern : Won't this update and overwrite if a workspace with the same ID exists?
            Ans : No, to update in pouchDB you need to pass in _id and rev. We are not passing rev. This will throw
            an exception if an entry with the same id exist.
            */
            const res = await this.workspaceDb.put({ _id: ws.name, ...ws });

            if (!res.ok) {
                const msg = `Failed to add Workspace to Table ${JSON.stringify(ws)}`;
                this.logger.log({ msg: msg, func: this.addWorkspace });
                throw new Error(msg);
            }

            const savedWorkspace = await this.workspaceDb.get(res.id) as WorkspaceModel;
            return savedWorkspace;
        } catch (err: any) {
            const msg = `Failed to add workspace due to error: ${err.message}`;
            this.logger.log({ msg: msg, func: this.addWorkspace });
            throw new Error(msg);
        }
    }

    async updateWorkspace(updatedWS: WorkspaceModel): Promise<boolean> {
        //To update a doc in PouchDB we need to pass latest rev
        this.logger.log({ msg: `Updating Workspace ${updatedWS.name}`, func: this.updateWorkspace });
        try {
            // Fetch the current version of the document. Will throw exception if it's missing
            const wsRaw = await this.workspaceDb.get(updatedWS.name);
            const rev = wsRaw._rev;
            const id = wsRaw._id;
            const ws = wsRaw as unknown as WorkspaceModel;
            if (!ws) {
                const msg = `Workspace ${updatedWS.name} doesn't exist`;
                this.logger.log({ msg: msg, func: this.updateWorkspace });
                return false;
            }
            const updatedDoc = { ...updatedWS, _id: id, _rev: rev };
            const result = await this.workspaceDb.put(updatedDoc);
            if (!result.ok) {
                const msg = `Failed to update workspace ${updatedWS.name}`;
                this.logger.log({ msg: msg, func: this.updateWorkspace });
                return false;
            }
            this.logger.log({ msg: `Workspace ${updatedWS.name} updated successfully`, func: this.updateWorkspace });
            return true;

        } catch (err: any) {
            const msg = `Failed to update workspace due to error: ${err.message}`;
            this.logger.log({ msg: msg, func: this.updateWorkspace, category: Category.Error });
            return false;
        }
    }


    async getWorkspace(workspaceName: string): Promise<WorkspaceModel | undefined> {
        this.logger.log({ msg: `Getting workspace with id ${workspaceName}`, func: this.getWorkspace });
        try {
            const doc = await this.workspaceDb.get(workspaceName);
            let ws = doc as unknown as WorkspaceModel;
            if (!ws) {
                const msg = `Workspace ${workspaceName} not found`;
                this.logger.log({ msg: msg, func: this.getWorkspace });
                return undefined;
            }
            this.logger.log({ msg: `Got workspace with id ${workspaceName}: ${JSON.stringify(ws)}`, func: this.getWorkspace });
            return ws;
        } catch (err: any) {
            const msg = `Failed to get workspace due to error: ${err.message}`;
            this.logger.log({ msg: msg, func: this.getWorkspace });
            return undefined;
        }
    }

    async deleteWorkspace(workspaceName: string): Promise<boolean> {
        this.logger.log({ msg: `Deleting workspace with ID ${workspaceName}`, func: this.deleteWorkspace });
        try {
            const doc = await this.workspaceDb.get(workspaceName);
            const result = await this.workspaceDb.remove({ _id: doc._id, _rev: doc._rev });
            if (!result.ok) {
                const msg = `Failed to delete workspace ${workspaceName}`;
                this.logger.log({ msg: msg, func: this.deleteRecord });
                return false;
            }
            this.logger.log({ msg: `Successfully deleted workspace with ID ${workspaceName}`, func: this.deleteWorkspace });
            return true;
        } catch (err: any) {
            const msg = `Failed to delete workspace due to error: ${err.message}`;
            this.logger.log({ msg: msg, func: this.deleteWorkspace });
            return false
        }
    }

    async getWorkspaces(skip: number, limit: number): Promise<WorkspaceModel[]> {
        this.logger.log({ msg: `Getting workspaces with limit ${limit} and skip ${skip}`, func: this.getWorkspaces });
        try {
            const result = await this.workspaceDb.allDocs({ skip: skip, limit: limit, include_docs: true });
            const docs = result.rows;
            this.logger.log({ msg: `Iterating docs of count ${docs.length}`, func: this.getWorkspaces });
            const wss: WorkspaceModel[] = docs.map((doc) => {
                const ws = doc.doc as unknown as WorkspaceModel;
                return ws;
            });
            this.logger.log({ msg: `Got workspaces ${JSON.stringify(wss)}`, func: this.getWorkspaces });
            return wss;
        } catch (err: any) {
            const msg = `Failed to get workspaces due to error: ${err.message}`;
            this.logger.log({ msg: msg, func: this.getWorkspaces });
            throw new Error(msg);
        }
    }

    // Record Operations
    async addRecord(record: RecordModel, workspaceID: string): Promise<RecordModel> {
        this.logger.log({ msg: `Adding record ${JSON.stringify(record)} for workspace ${workspaceID}`, func: this.addRecord });
        //Validate workspace
        try {
            const ws = await this.getWorkspace(workspaceID);
            if (!ws) {
                const msg = `Failed to add record. Invalid workspace ID ${workspaceID}`;
                this.logger.log({ msg: msg, func: this.addRecord, category: Category.Error });
                throw new Error(msg);
            }
            //Validate attributes
            validateAttribute(record.attributes);
            /*
            Concern : What will happen if a record with the same name already exists? Won't it get overwritten with this?
            Ans : No, to update we need to pass rev which we do not.
            */
            const addResult = await this.recordDB.put({ _id: generateProjectID(record.name, workspaceID), ...record })
            if (!addResult.ok) {
                const msg = `Failed to add record ${JSON.stringify(record)}`;
                this.logger.log({ msg: msg, func: this.addRecord })
                throw new Error(msg)
            }
            return record;
        } catch (err: any) {
            const msg = `Failed to add record due to error: ${err.message}`;
            this.logger.log({ msg: msg, func: this.addRecord, category: Category.Error });
            throw new Error(msg);
        }
    }
    updateRecord(updatedRecord: RecordModel, workspaceID: string): Promise<boolean> {
        throw new Error("NOT IMPLEMENTED. Need to decide how to update record properly.")
    }
    async getRecord(recordName: string, workspaceID: string): Promise<RecordModel> {
        this.logger.log({ msg: `Getting record with name ${recordName} in workspace ${workspaceID}`, func: this.getRecord });
        try {
            const doc = await this.recordDB.get(generateProjectID(recordName, workspaceID)) as RecordModel;
            if (!doc) {
                const msg = `Record ${recordName} not found in workspace ${workspaceID}`;
                this.logger.log({ msg: msg, func: this.getRecord });
                throw new Error(msg);
            }
            return doc;
        } catch (err: any) {
            const msg = `Failed to get record due to error: ${err.message}`;
            this.logger.log({ msg: msg, func: this.getRecord });
            throw new Error(msg);
        }
    }

    async deleteRecord(recordName: string, workspaceID: string): Promise<boolean> {
        this.logger.log({ msg: `Deleting record with name ${recordName} in workspace ${workspaceID}`, func: this.deleteRecord });
        try {
            const doc = await this.recordDB.get(recordName);
            if (!doc) {
                const msg = `Record ${recordName} not found in workspace ${workspaceID}`;
                this.logger.log({ msg: msg, func: this.getRecord });
                return false;
            }
            const result = await this.recordDB.remove({ _id: doc._id, _rev: doc._rev });
            if (!result.ok) {
                const msg = `Failed to delete record ${recordName}`;
                this.logger.log({ msg: msg, func: this.deleteRecord });
                return false;
            }
            this.logger.log({ msg: `Successfully deleted record with name ${recordName}`, func: this.deleteRecord });
            return true;
        } catch (err: any) {
            const msg = `Failed to delete record due to error: ${err.message}`;
            this.logger.log({ msg: msg, func: this.deleteRecord });
            throw new Error(msg);
        }
    }
    async getRecords(workspaceID: string, skip: number, limit: number): Promise<RecordModel[]> {
        throw new Error("Not yet implemented")
    }
}
