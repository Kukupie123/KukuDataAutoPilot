import { WorkspaceService } from "../WorkspaceService";
import { BuildOption } from "./BuildOptions";

export class WorkspaceFactory {
    public static async Build(option?: BuildOption): Promise<WorkspaceService> {
        const workspace = new WorkspaceService();
        await workspace.init();
        return workspace;
    }
}