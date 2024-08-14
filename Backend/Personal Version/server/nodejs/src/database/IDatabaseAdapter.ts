export interface IDatabaseAdapter {
    init(): Promise<void>;
    dispose(): Promise<void>;
}