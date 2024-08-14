export class WorkspaceModel {
    public name: string;
    public desc?: string;
    public created?: Date;
    public updated?: Date;

    public constructor(name: string, desc?: string, created?: Date, updated?: Date) {
        this.name = name;
        this.desc = desc;
        this.created = created;
        this.updated = updated;
    }
}