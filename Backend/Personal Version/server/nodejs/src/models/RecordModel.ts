export class RecordModel {
    name: string;
    desc?: string;
    workspaceID: string;
    created?: Date;
    updated?: Date;
    _id?: string;
    attributes: string;


    constructor(
        name: string,
        workspaceID: string,
        attributes: string,
        desc?: string,
        created?: Date,
        updated?: Date,
        id?: string
    ) {
        this.name = name;
        this.workspaceID = workspaceID;
        this.attributes = attributes;
        this.desc = desc;
        this.created = created;
        this.updated = updated;
        this._id = id;
    }
}