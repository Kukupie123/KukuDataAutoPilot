import { WorkspaceModel } from "../../../models/WorkspaceModel";

export interface IDatabaseAdapter {
    /**
     * Initialize the database and validate if required collection/tables are present.
     * @returns A promise that resolves when the initialization is complete.
     */
    init(): Promise<void>;
    dispose(): Promise<void>;
}
