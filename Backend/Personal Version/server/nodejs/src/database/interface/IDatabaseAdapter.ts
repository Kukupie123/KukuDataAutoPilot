// IDatabaseAdapter.ts
import { IWorkspaceAdapter } from "./IWorkspaceAdapter";
import { IRecordAdapter } from "./IRecordAdapter";

export interface IDatabaseAdapter extends IWorkspaceAdapter, IRecordAdapter {
    init(): Promise<void>;
    dispose(): Promise<void>;
}
