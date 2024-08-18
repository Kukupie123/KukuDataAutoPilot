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
    addRecord(record: RecordModel, workspaceID: string): Promise<RecordModel>;
    updateRecord(updatedRecord: RecordModel, workspaceID: string): Promise<boolean>;
    getRecord(recID: string, workspaceID: string): Promise<RecordModel>;
    deleteRecord(recID: string, workspaceID: string): Promise<boolean>;
    getRecords(skip: number, limit: number, workspaceID: string, recID: string): Promise<RecordModel[]>;

    //Workspace
    addWorkspace(workspaceName: string, description?: string): Promise<WorkspaceModel>;
    updateWorkspace(updatedWS: WorkspaceModel): Promise<boolean>
    getWorkspace(workspaceName: string): Promise<WorkspaceModel>;
    deleteWorkspace(id: string): Promise<boolean>;
    getWorkspaces(skip: number, limit: number): Promise<WorkspaceModel[]>;

}