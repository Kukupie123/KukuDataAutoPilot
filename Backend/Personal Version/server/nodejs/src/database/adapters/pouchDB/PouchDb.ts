import {IDatabaseAdapter} from "../../interface/IDatabaseAdapter";
import {WorkspaceModel} from "../../../models/WorkspaceModel";
import databasePouch from "pouchdb";
import {KDAPLogger} from "../../../util/KDAPLogger";
import {workspaceDbDir, recordDbDir, wsRecIndexDbDir, recWsIndexDbDir} from "./PouchHelper";
import {IRecordAttributeInfo, RecordModel} from "../../../models/RecordModel";
import {PouchDbWorkspace} from "./PouchDbWorkspace";
import {PouchDbRecord} from "./PouchDbRecord";
import {PouchDbIndexTable} from "./PouchDbIndexTable";

export class PouchDb implements IDatabaseAdapter {
    private pouchWorkspace!: PouchDbWorkspace;
    private pouchRecord!: PouchDbRecord;
    private pouchDbIndexTable!: PouchDbIndexTable;
    private logger = new KDAPLogger(PouchDb.name);
    public workspaceDb!: PouchDB.Database;
    public recordDB!: PouchDB.Database;
    public wsRecIndexDB!: PouchDB.Database;
    public recWsIndexDB!: PouchDB.Database;

    async init(): Promise<void> {
        this.logger.log({msg: "Initializing Pouch", func: this.init});
        try {
            //Creating pouchDb tables
            this.workspaceDb = new databasePouch(workspaceDbDir);
            this.recordDB = new databasePouch(recordDbDir);
            this.wsRecIndexDB = new databasePouch(wsRecIndexDbDir);
            this.recWsIndexDB = new databasePouch(recWsIndexDbDir);
            //Create instance of composite pouchDb
            this.pouchWorkspace = new PouchDbWorkspace(this);
            this.pouchRecord = new PouchDbRecord(this);
            this.pouchDbIndexTable = new PouchDbIndexTable(this);
            this.logger.log({msg: "Initialized Pouch", func: this.init});
            this.logger.log({msg: `Setting up listeners`, func: this.init});
        } catch (err: any) {
            this.logger.log({msg: `Failed to initialize PouchDb: ${err.message}`, func: this.init});
            throw err;
        }
    }

    async dispose(): Promise<void> {
    }

    async addWorkspace(workspaceName: string, description?: string | undefined): Promise<boolean> {
        return await this.pouchWorkspace.addWorkspace(workspaceName, description);
    }

    async updateWorkspace(updatedWS: WorkspaceModel): Promise<boolean> {
        return await this.pouchWorkspace.updateWorkspace(updatedWS);
    }

    async getWorkspace(workspaceName: string): Promise<WorkspaceModel | undefined> {
        return await this.pouchWorkspace.getWorkspace(workspaceName);
    }

    async deleteWorkspace(workspaceName: string): Promise<boolean> {
        return await this.pouchWorkspace.deleteWorkspace(workspaceName);
    }

    async getWorkspaces(skip: number, limit: number): Promise<WorkspaceModel[]> {
        return await this.pouchWorkspace.getWorkspaces(skip, limit);
    }

    async addRecord(name: string, attributes: Map<string, IRecordAttributeInfo>, desc?: string | undefined): Promise<boolean> {
        return await this.pouchRecord.addRecord(name, attributes, desc);
    }

    async getRecord(recID: string): Promise<RecordModel | undefined> {
        return await this.pouchRecord.getRecord(recID);
    }

    async deleteRecord(recID: string): Promise<boolean> {
        return await this.pouchRecord.deleteRecord(recID);
    }

    async getRecords(skip: number, limit: number): Promise<RecordModel[]> {
        return await this.pouchRecord.getRecords(skip, limit);
    }

    async link(recordIDWorkspaceIDTuples: [string, string][]): Promise<string[]> {
        return await this.pouchDbIndexTable.link(recordIDWorkspaceIDTuples);
    }


}
