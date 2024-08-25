export interface ICusomtIndexAdapter {
    /**
     * Link workspaces and records
     * @returns Array of workspace IDs that failed to link
     * @param recordIDWorkspaceIDTuples List of Tuple of recID:workspaceID
     */
    link(recordIDWorkspaceIDTuples: [string, string][]): Promise<string[]>;

    getRecordsOfWorkspace(workspaceID: string): Promise<string[]>;

    getWorkspaceOfRecord(recordID: string): Promise<string[]>;
}
