import { Logger as WintonLogger } from "winston";
import { WorkspaceService } from "../WorkspaceService";
import { BuildOption } from "./BuildOptions";
import { Logger as UtilLogger } from "../../../util/Logger";

export class WorkspaceFactory {
    private static Logger: WintonLogger = UtilLogger.CreateLogger(WorkspaceFactory.name);
    public static async Build(option?: BuildOption): Promise<WorkspaceService> {
        this.Logger.info(`Building Workspace Service with option ${option}`);
        const workspace = new WorkspaceService();
        this.Logger.info(`Initializing Workspace service`);
        await workspace.init();
        this.Logger.info(`Initialized Workspace Service`);
        return workspace;
    }
}