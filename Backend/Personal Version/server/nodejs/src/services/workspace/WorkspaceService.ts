import { DatabaseFactory } from "../../database/Factory/DatabaseFactory";
import { IDatabaseAdapter } from "../../database/IDatabaseAdapter";
import { WorkspaceModel } from "../../models/WorkspaceModel";
import { EventEmitter } from "events"
import { WorkspaceEvent } from "./Event/WorkspaceEventEnum"
import { Logger as WintonLogger } from "winston";
import { Logger as UtilLogger } from "../../util/Logger";
export class WorkspaceService {
    private logger: WintonLogger = UtilLogger.CreateLogger(WorkspaceService.name);
    private db!: IDatabaseAdapter;
    private static WorkspaceEventEmitter: EventEmitter = new EventEmitter(); // To be used for dispatching event

    public async init(): Promise<void> {
        this.logger.info("Initializing Workspace Service");
        this.db = await DatabaseFactory.Build();
        this.logger.info("Initialized Workspace Service");
    }

    /**
     * Creates a workspace in database, notifies listeners and returns the added workspace entry.
     */
    async createWorkspace(workspace: WorkspaceModel): Promise<WorkspaceModel> {
        this.logger.log("request", `Create Workspace Called with Payload ${JSON.stringify(workspace)}`);
        const wp = await this.db.createWorkspace(workspace);
        this.logger.log("request", `Created New Workspace. Dispatching event ${JSON.stringify(wp)}`);
        WorkspaceService.WorkspaceEventEmitter.emit(WorkspaceEvent.OnWorkspaceCreated.toString(), wp);
        return wp;
    }
}
