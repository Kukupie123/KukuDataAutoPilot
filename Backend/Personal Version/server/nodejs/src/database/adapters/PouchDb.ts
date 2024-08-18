import { IDatabaseAdapter } from "../IDatabaseAdapter";
import { WorkspaceModel } from "../../models/WorkspaceModel";
import databasePouch from "pouchdb";
import { KDAPLogger } from "../../util/KDAPLogger";
import { Category } from "../../config/kdapLogger.config";
import { RecordModel } from "../../models/RecordModel";
import { createDirectory } from "../../helper/DirHelper";
import { ResponseException } from "../../models/exception/ResponseException";

export class PouchDb implements IDatabaseAdapter {
    //TODO: Refactor Database to do purely database related stuff without doing other unrelated stuff. Using Event system to dispatch events
    private logger = new KDAPLogger(PouchDb.name);
    private workspaceDb!: PouchDB.Database;
    private recordDb!: PouchDB.Database;

    //Pouch Db config
    private rootDir = "data/db/pouch/";
    private workspaceDbDir = `${this.rootDir}/workspace`;
    private recordDbDir = `${this.rootDir}/record`;
    private userTableDir: string = `${this.rootDir}/user-table/`;

    async init(): Promise<void> {
        this.logger.log("Initializing PouchDb");
        // In PouchDb each table is it's own database
        this.workspaceDb = new databasePouch(this.workspaceDbDir);
        this.recordDb = new databasePouch(this.recordDbDir)
        this.logger.log("Initialized PouchDb");
    }

    async dispose(): Promise<void> {
    }

    async createRecord(workspaceID: string, record: RecordModel): Promise<RecordModel> {
        /* 1. Validate record, especially the attribute. It needs to have _id and it's attributes need to specify type and optional/mandatory
         * 2. Add record in record's table
         * 3. Update Workspace by adding this newly added record
         * 4. Create a new table in user-tables/workspaceID/recordID* with dummy values based on attributes
         */

        //Validate record's attribute
        //TODO: Turn this into a helper function
        const attributes = JSON.parse(record.attributes);
        if (!attributes['_id']) {
            throw new Error("_id is missing from attributes")
        }
        //iterate attribute key and validate
        for (const k in attributes) {
            const value = attributes[k] as string;
            const [dataType, importance] = value.split(";")
            //Eg :- importance = "kuku" -> if (!true || !(false)) -> if (!true || true)
            if (!importance || !(importance === "mandatory" || importance === "optional")) {
                throw new Error("Importance value not defined please specify if its optional or mandatory.Eg :- 'name':'text;mandatory'")
            }
            switch (dataType as string) {
                case "int":
                case "float":
                case "text":
                case "date":
                    break;
                default: throw new Error(`Invalid data type for attribute ${k}`)
            }
        }

        //Validate if workspaceID is valid
        let ws = await this.workspaceDb.get(workspaceID) as WorkspaceModel;
        this.logger.log(`WS : ${JSON.stringify(ws)}`)
        if (!ws) {
            throw new Error(`WorkspaceID ${workspaceID} doesn't exist`)
        }

        //Add record
        const result = await this.recordDb.put({ _id: record.name, ...record });
        if (!result.ok) {
            throw new Error(`Failed to create record ${record.name} for workspace ${workspaceID}`)
        }

        //Update workspace's record data
        //let rec = ws.records as string;
        // let parsed: any[] = JSON.parse(rec); //"[1,2,3]""
        // parsed.push(record.name);
        // Fetch the latest revision of the workspace document. This is required to update the document
        // Update the document with the new records
        //ws.records = JSON.stringify(parsed);

        // Try to put the updated document back into the database
        const updateResult = await this.workspaceDb.put({ _id: ws.name, ...ws });


        //Create user-table based on rec's attribute
        const userTableDir = `${this.userTableDir}/${ws.name}/${record.name}`;
        createDirectory(userTableDir);
        let recTable = new databasePouch(userTableDir,); //create the database
        return record;
    }

    async getRecords(skip: number, limit: number): Promise<RecordModel[]> {
        const result = await this.recordDb.allDocs({ skip: skip, limit: limit, include_docs: true });
        const recordss: RecordModel[] = result.rows.map((row) => {
            const recc = row.doc as unknown as RecordModel;
            this.logger.log(`record : ${JSON.stringify(recc)}`)
            return recc
        })
        return recordss;
    }


    async addRecordEntry(workspaceID: string, recordID: string, entry: any) {
        /**
         * 1. Validate entry structure. It needs to have _id and attributes need to have type and optional/mandatory
         * 2. Get record's attributes json and parse it
         * 3. Match them 
         * 4.  If all is good then get dynamically created record table
         * 5. add them
         */
    }


    //Workspace
    async addWorkspace(workspaceName: string, description?: string): Promise<WorkspaceModel> {
        this.logger.log(`Adding new workspace ${workspaceName}, ${description}`);
        // Prepare a new workspace DTO without `_rev` since it's a new document
        const ws: WorkspaceModel = new WorkspaceModel(workspaceName, description);

        // Save the new workspace to the database
        const result = await this.workspaceDb.put({ _id: ws.name, ...ws });
        if (!result.ok) {
            const msg = `Failed to add Workspace to Table ${JSON.stringify(ws)}`;
            this.logger.log(msg, Category.Error);
            throw new ResponseException(msg, 500);
        }

        //Functions are not transferred so doing savedWorkspace.foo() will not work. If it did I would have used some other way
        const savedWorkspace = await this.workspaceDb.get(result.id) as WorkspaceModel;
        return savedWorkspace;

    }

    async getWorkspace(workspaceName: string): Promise<WorkspaceModel> {
        const doc = await this.workspaceDb.get(workspaceName);
        let ws = doc as unknown as WorkspaceModel;
        return ws;
    }

    async deleteWorkspace(workspaceName: string): Promise<boolean> {
        //Not using getWorkspace function as we need "_rev" attribute to delete
        this.logger.log(`Deleting workspace with ID ${workspaceName}`);
        try {
            const doc = await this.workspaceDb.get(workspaceName);
            await this.workspaceDb.remove(doc._id, doc._rev);
            this.logger.log(`Successfully deleted workspace with ID ${workspaceName}`);
            return true;
        } catch (error) {
            this.logger.log(`Error deleting workspace with ID ${workspaceName}`);
            return false;
        }
    }

    async getWorkspaces(skip: number, limit: number): Promise<WorkspaceModel[]> {
        const result = await this.workspaceDb.allDocs({ skip: skip, limit: limit });
        const docs = result.rows;
        const wss: WorkspaceModel[] = docs.map((doc) => {
            const ws = doc.doc as unknown as WorkspaceModel;
            return ws;
        })
        return wss;
    }
}
