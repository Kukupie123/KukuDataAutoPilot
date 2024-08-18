import { IDatabaseAdapter } from "../../IDatabaseAdapter";
import { WorkspaceModel } from "../../../models/WorkspaceModel";
import databasePouch from "pouchdb";
import { KDAPLogger } from "../../../util/KDAPLogger";
import { Category } from "../../../config/kdapLogger.config";
import { RecordModel } from "../../../models/RecordModel";
import { ResponseException } from "../../../models/exception/ResponseException";
import { HttpStatusCode } from "../../../util/HttpCodes";
import { validateAttribute, workspaceDbDir, recordDbDir, getUserTableDirectory } from "./PouchHelper";
import { EventManager } from "../../../eventSystem/EventSystem";
import { RecordAddedEvent } from "../../../eventSystem/customEvents/RecordAddedEvent";

export class PouchDb implements IDatabaseAdapter {
    //TODO: Refactor Database to do purely database related stuff without doing other unrelated stuff. Using Event system to dispatch events
    private logger = new KDAPLogger(PouchDb.name);
    private workspaceDb!: PouchDB.Database;

    async init(): Promise<void> {
        this.logger.log("Initializing PouchDb");
        // In PouchDb each table is it's own database
        this.workspaceDb = new databasePouch(workspaceDbDir);
        this.logger.log("Initialized PouchDb");

        //Event listener setup
        EventManager.on(RecordAddedEvent, async (payload) => {
            this.logger.log(`Event Listener Triggered ${RecordAddedEvent.name}`)
            //TODO: Fix this
            if (payload.workspaceID === undefined) {
                this.logger.log(`WorkspaceID is null for added record ${payload.name}`);
                throw new Error();
            }
            this.logger.log(`Record Added. Adding recID ${payload.name} to workspace ${payload.workspaceID}`)
            const ws = await this.getWorkspace(payload.workspaceID!);
            ws.records.push(payload.name);
            await this.updateWorkspace(ws);
        })
    }

    async dispose(): Promise<void> {
    }

    //Workspace
    async addWorkspace(workspaceName: string, description?: string): Promise<WorkspaceModel> {
        this.logger.log(`Adding new workspace ${workspaceName}, ${description}`);
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

    async updateWorkspace(updatedWS: WorkspaceModel): Promise<boolean> {
        this.logger.log(`Updating Workspace ${updatedWS.name}`);
        const ws = await this.workspaceDb.get(updatedWS.name) as WorkspaceModel;
        if (!ws) {
            const msg = `Workspace ${updatedWS.name} doesn't exist`;
            this.logger.log(msg, Category.Error);
            throw new ResponseException(msg, HttpStatusCode.NOT_FOUND);
        }
        const result = await this.workspaceDb.put(updatedWS);
        if (!result.ok) {
            const msg = `Failed to update workspace ${updatedWS}`;;
            this.logger.log(msg, Category.Error);
            throw new ResponseException(msg, HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
        return true;
    }

    async getWorkspace(workspaceName: string): Promise<WorkspaceModel> {
        this.logger.log(`Getting workspace with id ${workspaceName}`)
        const doc = await this.workspaceDb.get(workspaceName);
        let ws = doc as unknown as WorkspaceModel;
        if (!ws) {
            const msg = `Workspace ${workspaceName} not found`;
            this.logger.log(msg, Category.Error);
            throw new ResponseException(msg, HttpStatusCode.NOT_FOUND);
        }
        this.logger.log(`Got workspace with id ${workspaceName} : ${JSON.stringify(ws)}`)
        return ws;
    }

    async deleteWorkspace(workspaceName: string): Promise<boolean> {
        this.logger.log(`Deleting workspace with ID ${workspaceName}`);
        const doc = await this.workspaceDb.get(workspaceName); //We need rev and ID to delete
        if (!doc) {
            const msg = `Workspace ${workspaceName} not found!`;
            this.logger.log(msg, Category.Error)
            throw new ResponseException(msg, HttpStatusCode.NOT_FOUND);
        }
        await this.workspaceDb.remove(doc._id, doc._rev);
        this.logger.log(`Successfully deleted workspace with ID ${workspaceName}`);
        return true;
    }

    async getWorkspaces(skip: number, limit: number): Promise<WorkspaceModel[]> {
        this.logger.log(`Getting workspaces with limit ${limit} and skip ${skip}`);
        const result = await this.workspaceDb.allDocs({ skip: skip, limit: limit, include_docs: true });
        const docs = result.rows;
        this.logger.log(`Iterating docs of count ${docs.length}`)
        const wss: WorkspaceModel[] = docs.map((doc) => {
            const ws = doc.doc as unknown as WorkspaceModel;
            return ws;
        })
        this.logger.log(`Got workspaces ${JSON.stringify(wss)}`);
        return wss;
    }


    //Record
    async addRecord(record: RecordModel, workspaceID: string): Promise<RecordModel> {
        this.logger.log(`Adding record ${JSON.stringify(record)} for workspace ${workspaceID}`);

        // Check if workspace exists
        this.logger.log(`Checking if workspace ${workspaceID} exists`);
        const ws = await this.getWorkspace(workspaceID);
        if (!ws) {
            const msg = `Failed to add record. Invalid workspace ID ${workspaceID}`;
            this.logger.log(msg);
            throw new ResponseException(msg, HttpStatusCode.BAD_REQUEST);
        }

        // Validate record's attributes
        this.logger.log(`Validating Attributes`);
        try {
            validateAttribute(record.attributes);
        } catch (error: any) {
            this.logger.log(`Attribute validation failed: ${error.message}`, Category.Error);
            throw new ResponseException(`Attribute validation failed: ${error.message}`, HttpStatusCode.BAD_REQUEST);
        }

        // Create & Get the record table dynamically in userTableDir/workspaceID/recordID
        this.logger.log(`Creating dynamic record table`);
        const recordDb = new databasePouch(getUserTableDirectory(workspaceID, record.name));

        // Check if the "table_info" document exists
        const existingDocs = await recordDb.allDocs({ keys: ["table_info"], include_docs: true });
        const infoEntryExists = existingDocs.rows.some(row => row.key === "table_info");

        if (!infoEntryExists) {
            this.logger.log(`No info_table entry found for ${record.name}. Creating new one`);
            await recordDb.put({ _id: "table_info", created: Date.now(), updated: Date.now() });
        }
        else {
            this.logger.log(`Existing info_table entry found skipping creating new entry.`)
        }
        this.logger.log(`Dispatching ${RecordAddedEvent.name} Event`)
        // Dispatch event
        EventManager.emit(new RecordAddedEvent(record)); //TODO: Fix this
        return record;
    }
    async updateRecord(updatedRecord: RecordModel, workspaceID: string): Promise<boolean> {
        return true;
    }

    async getRecord(recID: string, workspaceID: string): Promise<RecordModel> {
        const userTableDir = getUserTableDirectory(workspaceID, recID);
        const db = new databasePouch(userTableDir);
        const result = await db.get(recID) as RecordModel;
        return result;
    }
    async deleteRecord(recID: string, workspaceID: string): Promise<boolean> {
        const userTableDir = getUserTableDirectory(workspaceID, recID);

        this.logger.log(`Deleting rec ${recID}, workspace ${workspaceID} in dir ${userTableDir}`);
        const db = new databasePouch(userTableDir);
        await db.destroy();
        this.logger.log(`Deleted record ${recID} from workspace ${workspaceID}`);
        return true;
    }

    async getRecords(skip: number, limit: number, workspaceID: string, recID: string): Promise<RecordModel[]> {
        const db = new databasePouch(getUserTableDirectory(workspaceID, recID));
        const result = await db.allDocs({ skip: skip, limit: limit, include_docs: true });
        const docs = result.rows;
        const a = docs.map((doc) => {
            const id = doc.id;
            let rec = doc as unknown as RecordModel;
            rec.name = id;
            return rec;
        });
        return a;
    }
}
