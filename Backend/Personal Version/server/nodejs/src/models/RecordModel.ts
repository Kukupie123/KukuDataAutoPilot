export interface IRecordAttribute {
    [attributeName: string]: IRecordAttributeType;
}

export interface IRecordAttributeType {
    type: "int" | "float" | "text" | "date";
    importance: "important" | "optional"
}
export class RecordModel {
    name: string;
    desc?: string;
    workspaceID?: string;
    created?: Date;
    updated?: Date;
    attributes: IRecordAttribute[];


    constructor(
        name: string,
        attributes: IRecordAttribute[],
        workspaceID?: string,
        desc?: string,
        created?: Date,
        updated?: Date,
    ) {
        this.name = name;
        this.workspaceID = workspaceID;
        this.attributes = attributes;
        this.desc = desc;
        this.created = created;
        this.updated = updated;
    }
}