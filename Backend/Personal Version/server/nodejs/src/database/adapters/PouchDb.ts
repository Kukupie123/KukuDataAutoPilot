import { log } from "console";
import { IDatabaseAdapter } from "../IDatabaseAdapter";
import { sleep } from "../../util/Sleep";
import { WorkspaceModel } from "../../models/WorkspaceModel";
import databasePouch from "pouchdb";
/*
In PouchDb each database is treated as a document
*/
export class PouchDb implements IDatabaseAdapter {
    private workspaceDb!: PouchDB.Database;
    async init(): Promise<void> {
        log("Initializing PouchDb");
        this.workspaceDb = new databasePouch("KDAP_DB_WORKSPACE");
        log("Initialized PouchDb");
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