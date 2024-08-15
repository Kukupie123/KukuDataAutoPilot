export class WorkspaceModel {
    public name: string;
    public desc?: string;
    public created?: Date;
    public updated?: Date;
    public records?: string
    public _rev?: string;

    public constructor(name: string, desc?: string, created?: Date, updated?: Date, projects?: string, _rev?: string) {
        this.name = name;
        this.desc = desc;
        this.created = created;
        this.updated = updated;
        this.records = projects;
        this._rev = _rev;
    }
}