export interface IRecordAttributeInfo {
    attributeType: "int" | "float" | "text" | "date";
    attributeImportance: "important" | "optional";
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