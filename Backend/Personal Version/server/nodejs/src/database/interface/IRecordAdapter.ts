import { RecordModel } from "../../models/RecordModel";

export interface IRecordAdapter {
    /**
     * Add a new record
     * Note for Devs :- Only Create a new record entry.
     */
    addRecord(record: RecordModel): Promise<RecordModel>;

    /**
     * Update existing record
     */
    updateRecord(updatedRecord: RecordModel): Promise<boolean>;

    /**
     * Get record by ID
     */
    getRecord(recID: string): Promise<RecordModel | undefined>;

    /**
     * Delete a record by ID
     */
    deleteRecord(recID: string): Promise<boolean>;

    /**
     * Returns all the records of a workspace
     * @param workspaceID 
     * @param skip How many to skip
     * @param limit How many to get at once
     * @returns  List of Records
     */
    getRecords(workspaceID: string, skip: number, limit: number): Promise<RecordModel[]>;
}
