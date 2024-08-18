import { DatabaseFactory } from "../../database/Factory/DatabaseFactory";
import { IDatabaseAdapter } from "../../database/IDatabaseAdapter";
import { WorkspaceModel } from "../../models/WorkspaceModel";
import { KDAPLogger } from "../../util/KDAPLogger";
import { IService } from "../IService";
//TODO: Common response model
export class WorkspaceService implements IService {
    private logger = new KDAPLogger(WorkspaceService.name);
    private db!: IDatabaseAdapter;

    async initService(): Promise<void> {
        this.logger.log("Initializing Workspace Service");
        this.db = await DatabaseFactory.Build();
        this.logger.log("Initialized Workspace Service");
    }

    async addWorkspace(workspaceName: string, workspaceDesc: string = ""): Promise<WorkspaceModel> {
        this.logger.log(`Add Workspace with name ${workspaceName}, ${workspaceDesc}`);
        const ws = await this.db.addWorkspace(workspaceName, workspaceDesc);
        this.logger.log(`Workspace added : ${JSON.stringify(ws)}`)
        return ws;
    }

    async getWorkspace(id: string): Promise<WorkspaceModel> {
        this.logger.log(`Getting workspace with id ${id}`)
        const ws = await this.db.getWorkspace(id);
        this.logger.log(`Workspace retreived : ${JSON.stringify(ws)}`)
        return ws;
    }

    async getWorkspaces(skip: number = 0, limit: number = 10): Promise<WorkspaceModel[]> {
        this.logger.log(`GetWorkspaces with limit : ${limit} and skip : ${skip}`);
        const wss = await this.db.getWorkspaces(skip, limit);
        this.logger.log(`Workspaces retreived = ${JSON.stringify(wss)}`);
        return wss;
    }

    async deleteWorkspace(id: string): Promise<boolean> {
        this.logger.log(`Delete workspace with id ${id}`)
        await this.db.deleteWorkspace(id);
        this.logger.log(`Deleted workspace with ID ${id}`)
        return true;
    }
}
