import { WorkspaceModel } from "../../models/WorkspaceModel";

export interface IWorkspaceAdapter {
    /**
     * Adds new Workspace. Throws exception if one already exists
     * @throws Throws Exception if workspace already exists
     */
    addWorkspace(workspaceName: string, description?: string): Promise<boolean>;

    /**
     * Updates Existing workspace
     * @throws Throws exception if no existing workspace found
     */
    updateWorkspace(updatedWS: WorkspaceModel): Promise<boolean>;

    /**
     * Get workspace by name
     * @throws Throws exception if workspace doesn't exist
     */
    getWorkspace(workspaceName: string): Promise<WorkspaceModel | undefined>;

    /**
     * Delete workspace by name
     * @throws Throws exception if workspace doesn't exist
     */
    deleteWorkspace(workspaceName: string): Promise<boolean>;

    /**
     * Returns all workspaces
     */
    getWorkspaces(skip: number, limit: number): Promise<WorkspaceModel[]>;
}
