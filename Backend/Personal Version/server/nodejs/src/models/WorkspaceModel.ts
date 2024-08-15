export class WorkspaceModel {
    public name: string;
    public desc?: string;
    public created?: Date;
    public updated?: Date;
    public projects?: string

    public constructor(name: string, desc?: string, created?: Date, updated?: Date, projects?: string) {
        this.name = name;
        this.desc = desc;
        this.created = created;
        this.updated = updated;
        this.projects = projects;
    }
}