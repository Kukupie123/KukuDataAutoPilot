export class WorkspaceModel {
    public name: string;
    public desc?: string;
    public created?: Date;
    public updated?: Date;
    public records?: string[]

    constructor(name: string, desc?: string, created?: Date, updated?: Date, records?: string[]) {
        this.name = name;
        this.desc = desc;
        this.created = created;
        this.updated = updated;
        this.records = records;
    }
}