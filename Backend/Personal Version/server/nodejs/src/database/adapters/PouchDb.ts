import { log } from "console";
import { IDatabaseAdapter } from "../IDatabaseAdapter";
import { sleep } from "../../util/Sleep";
import { WorkspaceModel } from "../../models/WorkspaceModel";
import databasePouch from "pouchdb";
import { KDAPLogger } from "../../util/EnhancedLogger";
import { Category } from "../../config/kdapLogger.config";
export class PouchDb implements IDatabaseAdapter {
    private logger = new KDAPLogger(PouchDb.name);
    private workspaceDb!: PouchDB.Database;
    async init(): Promise<void> {
        this.logger.log(Category.Database, "Initializing PouchDb");
        //In PouchDb each database is treated as a document
        this.workspaceDb = new databasePouch("KDAP_DB_WORKSPACE");
        this.logger.log(Category.Database, "Initialized PouchDb");
    }

    async dispose(): Promise<void> {
        log("Fake dispose started");
        await sleep(500);
        log("fake dispose done");
    }

    async createWorkspace(workspace: WorkspaceModel): Promise<WorkspaceModel> {
        if (!workspace.name) {
            throw new Error("Name is missing in workspace object")
        }
        workspace.created = new Date(Date.now());
        workspace.updated = new Date(Date.now());

        const result = await this.workspaceDb.put({ _id: workspace.name, ...workspace });
        if (result.ok) {
            return workspace;
        }
        throw new Error("Failed to create workspace");
    }
}