import { IDatabaseAdapter } from "../IDatabaseAdapter";
import { sleep } from "../../util/Sleep";
import { WorkspaceModel } from "../../models/WorkspaceModel";
import databasePouch from "pouchdb";
import { KDAPLogger } from "../../util/EnhancedLogger";
import { Category } from "../../config/kdapLogger.config";

export class PouchDb implements IDatabaseAdapter {
    private logger = new KDAPLogger(PouchDb.name, Category.Database);
    private workspaceDb!: PouchDB.Database;
    private projectDb!: PouchDB.Database;
    private connector_projectToWorkspaceDB!: PouchDB.Database;
    private connector_workspaceToWorkspaceDB!: PouchDB.Database;

    async init(): Promise<void> {
        this.logger.log("Initializing PouchDb");
        // In PouchDb each table is it's own database
        this.workspaceDb = new databasePouch("data/db/pouch/workspace");
        this.projectDb = new databasePouch("data/db/pouch/project")
        this.connector_projectToWorkspaceDB = new databasePouch("data/db/pouch/projectToWorkspaceConnectorDB")
        this.connector_workspaceToWorkspaceDB = new databasePouch("data/db/pouch/workspaceToWorkspaceConnectorDB")

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
                interface WorkspaceDocument {
                    _id: string;       // Equivalent to name
                    desc?: string;
                    created: string;  // Dates stored as ISO strings
                    updated: string;  // Dates stored as ISO strings
                }
                const doc = row.doc as unknown as WorkspaceDocument;
                this.logger.log(`Current DOC : ${JSON.stringify(doc)}`)

                if (doc) {
                    // Parse dates from strings if they exist
                    const created = new Date(doc.created);
                    const updated = new Date(doc.updated);

                    // Create and return a new WorkspaceModel instance
                    const wp = new WorkspaceModel(
                        doc._id, // name is same as _id
                        doc.desc,
                        created,
                        updated
                    );
                    this.logger.log(`Parsed doc = ${JSON.stringify(wp)}`)
                    return wp;
                }
                return undefined; // Handle case where doc might be undefined
            }).filter((workspace): workspace is WorkspaceModel => workspace !== undefined); // Filter out undefined values

            this.logger.log(`Mapped ${workspaces.length} workspaces to models`);
            // Return the array of workspaces
            return workspaces;
        } catch (error) {
            // Handle errors appropriately
            this.logger.log(`Error fetching workspaces: ${JSON.stringify(error)}`);
            throw new Error('Unable to fetch workspaces');
        }
    }
}
