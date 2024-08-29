export interface ICusomtIndexAdapter {
    /**
     * Link workspaces and records
     * @returns Array of workspace IDs that failed to link
     * @param recordIDWorkspaceIDTuples List of Tuple of recID:workspaceID
     */
    link(recordIDWorkspaceIDTuples: [string, string][]): Promise<[string, string][]>;

    getRecordsOfWorkspace(workspaceID: string, skip: number, limit: number): Promise<string[]>;

    getWorkspacesOfRecord(recordID: string, skip: number, limit: number): Promise<string[]>;

    deleteLink(workspaceID: string, recordID: string): Promise<boolean>;
}
