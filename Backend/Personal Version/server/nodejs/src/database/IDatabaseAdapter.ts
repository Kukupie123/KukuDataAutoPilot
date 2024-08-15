import { RecordModel } from "../models/RecordModel";
import { WorkspaceModel } from "../models/WorkspaceModel";

export interface IDatabaseAdapter {
    /**
     * Called by DatabaseFactory during Build
     */
    init(): Promise<void>;
    dispose(): Promise<void>;

    //Workspace
    createWorkspace(workspace: WorkspaceModel): Promise<WorkspaceModel>;
    getWorkspace(id: string): Promise<WorkspaceModel>;
    getWorkspaces(skip: number, limit: number): Promise<WorkspaceModel[]>;

    //Records
    createRecord(workspaceID: string, record: RecordModel): Promise<RecordModel>;
    getRecords(skip: number, limit: number): Promise<RecordModel[]>;

}