//Low level functions of database. To be implemented by required database adapters.
export interface IDatabaseAdapter {
    init(): Promise<void>;
    dispose(): Promise<void>;
}