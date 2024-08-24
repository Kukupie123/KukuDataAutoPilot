export interface ICusomtIndexAdapter {
    /**
     * Link workspaces and records
     * @returns Array of workspace IDs that failed to link
     * @param recordIDWorkspaceIDTuples Tuple list of recordID and workspaceID
     */
    link(recordIDWorkspaceIDTuples:[string:string][]): Promise<string[]>;
}
