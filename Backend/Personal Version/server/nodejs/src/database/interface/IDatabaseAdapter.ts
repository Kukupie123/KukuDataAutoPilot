// IDatabaseAdapter.ts
import { IWorkspaceAdapter } from "./IWorkspaceAdapter";
import { IRecordAdapter } from "./IRecordAdapter";

/**
 * Interface that has to be implemented by Db Adapters.
 */
export interface IDatabaseAdapter extends IWorkspaceAdapter, IRecordAdapter {
    init(): Promise<void>;
    dispose(): Promise<void>;
}
