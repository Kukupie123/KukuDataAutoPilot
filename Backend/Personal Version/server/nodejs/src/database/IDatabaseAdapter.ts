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
    //Task
    createWorkspace(workspace: WorkspaceModel): Promise<WorkspaceModel>;
    getWorkspace(id: string): Promise<WorkspaceModel>;
    getWorkspaces(skip: number, limit: number): Promise<WorkspaceModel[]>;

    //Records
    createRecord(workspaceID: string, record: RecordModel): Promise<RecordModel>;
    getRecords(skip: number, limit: number): Promise<RecordModel[]>;

    addWorkspace(workspaceName: string, description?: string): Promise<WorkspaceModel>;
    deleteWorkspace(id: string): Promise<boolean>;

}