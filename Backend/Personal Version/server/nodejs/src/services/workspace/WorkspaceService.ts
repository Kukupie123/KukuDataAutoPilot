import { DatabaseFactory } from "../../database/Factory/DatabaseFactory";
import { IDatabaseAdapter } from "../../database/IDatabaseAdapter";
import { WorkspaceModel } from "../../models/WorkspaceModel";
import { sleep } from "../../util/Sleep";

export class WorkspaceService {

    private db!: IDatabaseAdapter;

    public async init(): Promise<void> {
        console.log("Initializing Workspace Service");
        this.db = await DatabaseFactory.Build();
        console.log("Initialized Workspace Service");
    }

    /**
     * Creates a workspace in database and returns the added workspace entry.
     */
    async createWorkspace(workspace: WorkspaceModel): Promise<WorkspaceModel> {
        return await this.db.createWorkspace(workspace);
    }
}