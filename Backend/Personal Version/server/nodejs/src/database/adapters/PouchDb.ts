import { IDatabaseAdapter } from "../IDatabaseAdapter";
import { WorkspaceModel } from "../../models/WorkspaceModel";
import databasePouch from "pouchdb";
import { KDAPLogger } from "../../util/KDAPLogger";
import { Category } from "../../config/kdapLogger.config";
import { RecordModel } from "../../models/RecordModel";

export class PouchDb implements IDatabaseAdapter {
    private logger = new KDAPLogger(PouchDb.name, Category.Database);
    private workspaceDb!: PouchDB.Database;
    private recordDb!: PouchDB.Database;

    async init(): Promise<void> {
        this.logger.log("Initializing PouchDb");
        // In PouchDb each table is it's own database
        this.workspaceDb = new databasePouch("data/db/pouch/workspace");
        this.recordDb = new databasePouch("data/db/pouch/record")
        this.logger.log("Initialized PouchDb");
    }

    async dispose(): Promise<void> {
    }

    async createWorkspace(workspace: WorkspaceModel): Promise<WorkspaceModel> {
        if (!workspace.name) {
            throw new Error("Name is missing in workspace object");
        }
        workspace.created = new Date(Date.now());
        workspace.updated = new Date(Date.now());
        workspace.records = JSON.stringify([])

        const result = await this.workspaceDb.put({ _id: workspace.name, ...workspace });
        if (result.ok) {
            this.logger.log(`Workspace ${workspace.name} created successfully`);
            return workspace;
        }
        this.logger.log(`Failed to create workspace ${workspace.name}`);
        throw new Error("Failed to create workspace");
    }

    async getWorkspace(id: string): Promise<WorkspaceModel> {
        this.logger.log(`Attempting to get workspace with id ${id}`);
        const result = await this.workspaceDb.get(id) as WorkspaceModel;
        this.logger.log(`Retreived document is ${JSON.stringify(result)}`)
        return result;
    }

    async getWorkspaces(skip: number, limit: number): Promise<WorkspaceModel[]> {
        this.logger.log(`Fetching workspaces with skip=${skip} and limit=${limit}`);
        try {
            // Fetch all documents with pagination
            const result = await this.workspaceDb.allDocs({ skip: skip, limit: limit, include_docs: true });
            this.logger.log(`Fetched ${result.rows.length} workspaces`);

            // Map the docs into an array of WorkspaceModel
            const workspaces: WorkspaceModel[] = result.rows.map((row) => {
                this.logger.log(`Current Row : ${JSON.stringify(row)}`)
                const doc = row.doc as unknown as WorkspaceModel;
                this.logger.log(`Parsed doc = ${JSON.stringify(doc)}`)
                return doc;

            });

            this.logger.log(`Mapped ${workspaces.length} workspaces to models`);
            // Return the array of workspaces
            return workspaces;
        } catch (error) {
            // Handle errors appropriately
            this.logger.log(`Error fetching workspaces: ${JSON.stringify(error)}`);
            throw new Error('Unable to fetch workspaces');
        }
    }

    async createRecord(workspaceID: string, record: RecordModel): Promise<RecordModel> {
        /*
         * 2. Add record in record's table
         * 3. Update Workspace by adding this newly added record
         * 4. Create a new table in user-tables/workspaceID/recordID* with dummy values based on attributes
         */

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
        let rec = ws.records as string;
        let parsed: any[] = JSON.parse(rec); //"[1,2,3]""
        parsed.push(record.name);
        // Fetch the latest revision of the workspace document. This is required to update the document
        // Update the document with the new records
        ws.records = JSON.stringify(parsed);

        // Try to put the updated document back into the database
        const updateResult = await this.workspaceDb.put({ _id: ws.name, ...ws });

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
}
