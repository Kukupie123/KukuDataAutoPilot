import { Category } from "../../../config/kdapLogger.config";
import { KDAPLogger } from "../../../util/EnhancedLogger";
import { WorkspaceService } from "../WorkspaceService";
import { BuildOption } from "./BuildOptions";

export class WorkspaceFactory {
    private static Logger = new KDAPLogger(WorkspaceFactory.name);
    public static async Build(option?: BuildOption): Promise<WorkspaceService> {
        this.Logger.log(Category.Factory, `Building Workspace Service with option ${option}`);
        const workspace = new WorkspaceService();
        this.Logger.log(Category.Factory, `Initializing Workspace service`);
        await workspace.init();
        this.Logger.log(Category.Factory, `Initialized Workspace Service`);
        return workspace;
    }
}