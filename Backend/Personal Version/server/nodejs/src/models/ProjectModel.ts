export class ProjectModel {
    name: string;
    desc?: string;
    workspaceID: string;
    created?: Date;
    updated?: Date;
    attributes: string;

    constructor(
        name: string,
        workspaceID: string,
        attributes: string,
        desc?: string,
        created?: Date,
        updated?: Date
    ) {
        this.name = name;
        this.workspaceID = workspaceID;
        this.attributes = attributes;
        this.desc = desc;
        this.created = created;
        this.updated = updated;
    }
}