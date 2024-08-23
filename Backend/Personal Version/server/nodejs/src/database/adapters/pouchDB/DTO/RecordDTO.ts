import { IRecordAttributeInfo, RecordModel } from "../../../../models/RecordModel";

export class RecordDTO {
    _id: string;
    _rev?: string;
    name: string;
    desc?: string;
    attributes: string;  // This is the JSON string of attributes
    created: Date;
    updated: Date;

    constructor(name: string, attributes: string, created: Date, updated: Date, desc?: string, rev?: string) {
        this._id = name;
        this.name = name;
        this.attributes = attributes;
        this.created = created;
        this.updated = updated;
        this.desc = desc;

        if (rev) {
            this._rev = rev;
        }
    }

    public toRecordModel(): RecordModel {
        const parsedAttributes = JSON.parse(this.attributes) as [string, IRecordAttributeInfo][];
        const attributeMap = new Map<string, IRecordAttributeInfo>(parsedAttributes);
        return new RecordModel(this.name, attributeMap, this.desc, this.created, this.updated);
    }
}

//TODO: Make DTO for workspace for pouch if required for consistency