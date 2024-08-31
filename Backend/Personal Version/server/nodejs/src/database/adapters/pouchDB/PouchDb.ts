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

    addWorkspace(workspaceName: string, description?: string | undefined): Promise<boolean> {
        return this.pouchWorkspace.addWorkspace(workspaceName, description);
    }

    updateWorkspace(updatedWS: WorkspaceModel): Promise<boolean> {
        return this.pouchWorkspace.updateWorkspace(updatedWS);
    }

    getWorkspace(workspaceName: string): Promise<WorkspaceModel | undefined> {
        return this.pouchWorkspace.getWorkspace(workspaceName);
    }

    deleteWorkspace(workspaceName: string): Promise<boolean> {
        return this.pouchWorkspace.deleteWorkspace(workspaceName);
    }

    getWorkspaces(skip: number, limit: number): Promise<WorkspaceModel[]> {
        return this.pouchWorkspace.getWorkspaces(skip, limit);
    }

    addRecord(name: string, attributes: Map<string, IRecordAttributeInfo>, desc?: string | undefined): Promise<boolean> {
        return this.pouchRecord.addRecord(name, attributes, desc);
    }

    getRecord(recID: string): Promise<RecordModel | undefined> {
        return this.pouchRecord.getRecord(recID);
    }

    deleteRecord(recID: string): Promise<boolean> {
        return this.pouchRecord.deleteRecord(recID);
    }

    getRecords(skip: number, limit: number): Promise<RecordModel[]> {
        return this.pouchRecord.getRecords(skip, limit);
    }

    link(recordIDWorkspaceIDTuples: [string, string][]): Promise<[string, string][]> {
        return this.pouchDbIndexTable.link(recordIDWorkspaceIDTuples);
    }

    getRecordsOfWorkspace(workspaceID: string, skip: number = 0, limit: number = 999): Promise<string[]> {
        return this.pouchDbIndexTable.getRecordsOfWorkspace(workspaceID, skip, limit);
    }

    getWorkspacesOfRecord(recID: string, skip: number = 0, limit: number = 999): Promise<string[]> {
        return this.pouchDbIndexTable.getWorkspacesOfRecord(recID, skip, limit);
    }

    deleteLink(workspaceID: string, recordID: string): Promise<boolean> {
        return this.pouchDbIndexTable.deleteLink(workspaceID, recordID);
    }

}
