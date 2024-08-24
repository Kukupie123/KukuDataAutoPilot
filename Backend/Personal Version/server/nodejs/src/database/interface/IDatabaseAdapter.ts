// IDatabaseAdapter.ts
import { IWorkspaceAdapter } from "./IWorkspaceAdapter";
import { IRecordAdapter } from "./IRecordAdapter";
import {ICusomtIndexAdapter} from "./ICusomtIndexAdapter";

/**
 * Interface that has to be implemented by Db Adapters.
 */
export interface IDatabaseAdapter extends IWorkspaceAdapter, IRecordAdapter, ICusomtIndexAdapter {
    init(): Promise<void>;
    dispose(): Promise<void>;
}
