import { IRecordAttributeInfo, RecordModel } from "../../models/RecordModel";

export interface IRecordAdapter {
    /**
     * Add a new record
     */
    addRecord(name: string, attributes: Map<string, IRecordAttributeInfo>, desc?: string): Promise<boolean>;

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
    getRecords(skip: number, limit: number): Promise<RecordModel[]>;
}
