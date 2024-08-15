import { WorkspaceModel } from "../models/WorkspaceModel";

export interface IDatabaseAdapter {
    /**
     * Called by DatabaseFactory during Build
     */
    init(): Promise<void>;
    dispose(): Promise<void>;

    createWorkspace(workspace: WorkspaceModel): Promise<WorkspaceModel>;
    getWorkspace(id: string): Promise<WorkspaceModel>;
    getWorkspaces(skip: number, limit: number): Promise<WorkspaceModel[]>;

}