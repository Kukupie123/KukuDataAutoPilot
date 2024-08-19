import { RecordModel } from "../models/RecordModel";
import { WorkspaceModel } from "../models/WorkspaceModel";

export interface IDatabaseAdapter {
    /**
     * Called by DatabaseFactory during Build
     */
    init(): Promise<void>;
    dispose(): Promise<void>;

    //Workspace
    /*
    _id:String, Name:String, Created:Date, Updated:Date, RecordsID:jsonStringList
    */
    //Record
    /*
    _id:String, Name:String, Created:Date, Updated:Date, WorkspaceID:String, Attributes : JsonStringList
    */
    /*
    Structure of attribute :-
        {
            "_id" : "type;important"
            attributeName : type;importance
        }
        Types :- int, float, text, date
        Example :- 
        {
            "_id":"text;important"
            "RollNumber": "text;important",
            "DOB": "date;optional"
        }
    */

    //Records
    /**
     * Add a new record to the workspace.
     * @param record record model to add.
     * @param workspaceID workspaceID the record is for.
     */
    addRecord(record: RecordModel, workspaceID: string): Promise<RecordModel>;
    /**
     * Update existing record
     * @param updatedRecord Updated Record 
     * @param workspaceID ID of the workspace the record is from.
     */
    updateRecord(updatedRecord: RecordModel, workspaceID: string): Promise<boolean>;
    /**
     * Get record by ID
     * @param recID ID of the Record to get.
     * @param workspaceID  ID of the workspace the record is from..
     */
    getRecord(recID: string, workspaceID: string): Promise<RecordModel | undefined>;
    /**
     * Delete a record by ID
     * @param recID ID of the record to delete.
     * @param workspaceID ID of the workspace the record is from.
     */
    deleteRecord(recID: string, workspaceID: string): Promise<boolean>;
    /**
     * Returns all the records of a workspace
     * @param workspaceID 
     * @param skip How many to skip
     * @param limit How many to get at once
     * @returns  List of Records
     */
    getRecords(workspaceID: string, skip: number, limit: number): Promise<RecordModel[]>;

    //Workspace
    addWorkspace(workspaceName: string, description?: string): Promise<WorkspaceModel>;
    updateWorkspace(updatedWS: WorkspaceModel): Promise<boolean>
    getWorkspace(workspaceName: string): Promise<WorkspaceModel | undefined>;
    deleteWorkspace(id: string): Promise<boolean>;
    getWorkspaces(skip: number, limit: number): Promise<WorkspaceModel[]>;

}