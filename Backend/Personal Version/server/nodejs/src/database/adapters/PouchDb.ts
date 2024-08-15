import { log } from "console";
import { IDatabaseAdapter } from "../IDatabaseAdapter";
import { sleep } from "../../util/Sleep";
import { WorkspaceModel } from "../../models/WorkspaceModel";
import databasePouch from "pouchdb";
import { Logger as WintonLogger } from "winston";
import { Logger as UtilLogger } from "../../util/Logger";
export class PouchDb implements IDatabaseAdapter {
    private logger: WintonLogger = UtilLogger.CreateLogger(PouchDb.name);
    private workspaceDb!: PouchDB.Database;
    async init(): Promise<void> {
        this.logger.info("Initializing PouchDb");
        //In PouchDb each database is treated as a document
        this.workspaceDb = new databasePouch("KDAP_DB_WORKSPACE");
        this.logger.info("Initialized PouchDb");
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