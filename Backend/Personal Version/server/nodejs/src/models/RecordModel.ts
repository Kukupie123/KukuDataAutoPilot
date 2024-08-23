export enum RecordAttributeType{
    text = "text",
    int = "int",
    float = "float",
    date = "date",
}
export enum RecordImportance{
    important = "important",
    optional = "optional",
}
export interface IRecordAttributeInfo {
    attributeType: RecordAttributeType;
    attributeImportance: RecordImportance;
}

export class RecordModel {
    name: string;
    desc?: string;
    created?: Date;
    updated?: Date;
    attributes: Map<string, IRecordAttributeInfo>;


    constructor(
        name: string,
        attributes: Map<string, IRecordAttributeInfo>,
        desc?: string,
        created?: Date,
        updated?: Date,
    ) {
        this.name = name;
        this.attributes = attributes;
        this.desc = desc;
        this.created = created;
        this.updated = updated;
    }
}