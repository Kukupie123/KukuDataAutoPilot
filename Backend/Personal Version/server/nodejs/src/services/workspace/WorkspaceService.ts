import { DatabaseFactory } from "../../database/Factory/DatabaseFactory";
import { IDatabaseAdapter } from "../../database/IDatabaseAdapter";
import { WorkspaceModel } from "../../models/WorkspaceModel";
import { EventEmitter } from "events"
import { WorkspaceEvent } from "./Event/WorkspaceEventEnum"
import { KDAPLogger } from "../../util/EnhancedLogger";
import { Category } from "../../config/kdapLogger.config";
export class WorkspaceService {
    private logger = new KDAPLogger(WorkspaceService.name, Category.Service);
    private db!: IDatabaseAdapter;
    private static WorkspaceEventEmitter: EventEmitter = new EventEmitter(); // To be used for dispatching event

    public async init(): Promise<void> {
        this.logger.log("Initializing Workspace Service");
        this.db = await DatabaseFactory.Build();
        this.logger.log("Initialized Workspace Service");
    }

    /**
     * Creates a workspace in database, notifies listeners and returns the added workspace entry.
     */
    async createWorkspace(workspace: WorkspaceModel): Promise<WorkspaceModel> {
        this.logger.log(`Create Workspace Called with Payload ${JSON.stringify(workspace)}`);
        const ws = await this.db.createWorkspace(workspace);
        this.logger.log(`Created New Workspace. Dispatching event ${JSON.stringify(ws)}`);
        WorkspaceService.WorkspaceEventEmitter.emit(WorkspaceEvent.OnWorkspaceCreated.toString(), ws);
        return ws;
    }

    async getWorkspaces(skip: number = 0, limit: number = 10): Promise<WorkspaceModel[]> {
        this.logger.log(`GetWorkspaces with limit : ${limit} and skip : ${skip}`);
        const wss = await this.db.getWorkspaces(skip, limit);
        this.logger.log(`Workspaces retreived = ${JSON.stringify(wss)}`);
        return wss;
    }
}
