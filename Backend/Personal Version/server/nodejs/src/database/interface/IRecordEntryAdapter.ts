export interface IRecordEntryAdapter {
    /**
     * Add single entry to record.
     * @param recordID the record ID to add to
     * @param entryNameValue the attribute name along with the value
     */
    addEntryToRecord(recordID: string, entryNameValue: Map<string, any>): Promise<boolean>;

    /**
     * Remove an entry with it's ID
     * @param recordID
     * @param entryID
     */
    removeEntryFromRecord(recordID: string, entryID: string): Promise<boolean>;

    /**
     * Edit existing entry of a record
     * @param recordID
     * @param entryNameValue
     */
    editEntryFromRecord(recordID: string, entryNameValue: Map<string, any>): Promise<boolean>;
}
