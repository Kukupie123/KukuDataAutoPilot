import { DatabaseFactory } from "../../database/Factory/DatabaseFactory";
import { IDatabaseAdapter } from "../../database/interface/IDatabaseAdapter";
import { WorkspaceModel } from "../../models/WorkspaceModel";
import { KDAPLogger } from "../../util/KDAPLogger";
import { IService } from "../IService";
//TODO: Common response model
export class WorkspaceService implements IService {
    private logger = new KDAPLogger(WorkspaceService.name);
    private db!: IDatabaseAdapter;

    async initService(): Promise<void> {
        this.logger.log({ msg: "Initializing Workspace Service", func: this.initService });
        this.db = await DatabaseFactory.Build();
        this.logger.log({ msg: "Initialized Workspace Service", func: this.initService });
    }

    async addWorkspace(workspaceName: string, workspaceDesc: string = ""): Promise<boolean> {
        this.logger.log({ msg: `Add Workspace with name ${workspaceName}, ${workspaceDesc}`, func: this.addWorkspace });
        const result = await this.db.addWorkspace(workspaceName, workspaceDesc);
        this.logger.log({ msg: `Workspace added returned ${JSON.stringify(result)}`, func: this.addWorkspace })
        return result;
    }

    async updateWorkspace(updatedWorkspace: WorkspaceModel): Promise<boolean> {
        this.logger.log({ msg: `Updating workspace ${JSON.stringify(updatedWorkspace)}`, func: this.updateWorkspace });
        const result = await this.db.updateWorkspace(updatedWorkspace);
        this.logger.log({ msg: `Updated workspace result : ${result}`, func: this.updateWorkspace })
        return result;
    }

    async getWorkspace(id: string): Promise<WorkspaceModel | undefined> {
        this.logger.log({ msg: `Getting workspace with id ${id}`, func: this.getWorkspace })
        const ws = await this.db.getWorkspace(id);
        this.logger.log({ msg: `Workspace retreived : ${JSON.stringify(ws)}`, func: this.getWorkspace })
        return ws;
    }

    async getWorkspaces(skip: number = 0, limit: number = 10): Promise<WorkspaceModel[]> {
        this.logger.log({ msg: `GetWorkspaces with limit : ${limit} and skip : ${skip}`, func: this.getWorkspaces });
        const wss = await this.db.getWorkspaces(skip, limit);
        this.logger.log({ msg: `Workspaces retreived = ${JSON.stringify(wss)}`, func: this.getWorkspaces });
        return wss;
    }

    async deleteWorkspace(id: string): Promise<boolean> {
        this.logger.log({ msg: `Delete workspace with ID ${id}`, func: this.deleteWorkspace })
        const result = await this.db.deleteWorkspace(id);
        this.logger.log({ msg: `Deleted workspace Result ${result}`, func: this.deleteWorkspace })
        return result;
    }
}
