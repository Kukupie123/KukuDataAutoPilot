import { Category } from "../../../config/kdapLogger.config";
import { WorkspaceModel } from "../../../models/WorkspaceModel";
import { KDAPLogger } from "../../../util/KDAPLogger";

export class PouchDbWorkspace {
    private workspaceDb: PouchDB.Database;
    private logger: KDAPLogger;
    constructor(workspaceDb: PouchDB.Database) {
        this.logger = new KDAPLogger(PouchDbWorkspace.name);
        this.workspaceDb = workspaceDb;
    }

    // Workspace Operations
    async addWorkspace(workspaceName: string, description?: string): Promise<boolean> {
        this.logger.log({ msg: `Adding new workspace ${workspaceName}, ${description}`, func: this.addWorkspace });
        try {
            const ws: WorkspaceModel = new WorkspaceModel(workspaceName, description, new Date(Date.now()), new Date(Date.now()));

            /*
            Concern : Won't this update and overwrite if a workspace with the same ID exists?
            Ans : No, to update in pouchDB you need to pass in _id and rev. We are not passing rev. This will throw
            an exception if an entry with the same id exist.
            */
            const res = await this.workspaceDb.put({ _id: ws.name, ...ws }); //Throws exception if ID exists

            if (!res.ok) {
                const msg = `Failed to add Workspace to Table ${JSON.stringify(ws)}`;
                this.logger.log({ msg: msg, func: this.addWorkspace });
                return false;
            }

            const savedWorkspace = await this.workspaceDb.get(res.id) as WorkspaceModel;
            return true;
        } catch (err: any) {
            const msg = `Failed to add workspace due to error: ${err.message}`;
            this.logger.log({ msg: msg, func: this.addWorkspace });
            return false;
        }
    }

    async updateWorkspace(updatedWS: WorkspaceModel): Promise<boolean> {
        //To update a doc in PouchDB we need to pass latest rev
        this.logger.log({ msg: `Updating Workspace ${updatedWS.name}`, func: this.updateWorkspace });
        try {
            // Fetch the current version of the document.
            const wsRaw = await this.workspaceDb.get(updatedWS.name); //Throws exception if doesn't exist
            //Rev and ID required to update
            const rev = wsRaw._rev;
            const id = wsRaw._id;
            const ws = wsRaw as unknown as WorkspaceModel;
            if (!ws) {
                const msg = `Workspace ${updatedWS.name} doesn't exist`;
                this.logger.log({ msg: msg, func: this.updateWorkspace });
                return false;
            }
            const updatedDoc = { ...updatedWS, _id: id, _rev: rev };
            const result = await this.workspaceDb.put(updatedDoc); //Throws exception if rev is invalid or outdated
            if (!result.ok) {
                const msg = `Failed to update workspace ${updatedWS.name}`;
                this.logger.log({ msg: msg, func: this.updateWorkspace });
                return false;
            }
            this.logger.log({ msg: `Workspace ${updatedWS.name} updated successfully`, func: this.updateWorkspace });
            return true;

        } catch (err: any) {
            const msg = `Failed to update workspace due to error: ${err.message}`;
            this.logger.log({ msg: msg, func: this.updateWorkspace, category: Category.Error });
            return false;
        }
    }


    async getWorkspace(workspaceName: string): Promise<WorkspaceModel | undefined> {
        this.logger.log({ msg: `Getting workspace with id ${workspaceName}`, func: this.getWorkspace });
        try {
            const doc = await this.workspaceDb.get(workspaceName); //Throws exception if workspace doesn't exist
            let ws = doc as unknown as WorkspaceModel;
            if (!ws || ws.name === undefined) {
                const msg = `Workspace ${workspaceName} not found`;
                this.logger.log({ msg: msg, func: this.getWorkspace });
                return undefined;
            }
            this.logger.log({ msg: `Got workspace with id ${workspaceName}: ${JSON.stringify(ws)}`, func: this.getWorkspace });
            return ws;
        } catch (err: any) {
            const msg = `Failed to get workspace due to error: ${err.message}`;
            this.logger.log({ msg: msg, func: this.getWorkspace });
            return undefined;
        }
    }

    async deleteWorkspace(workspaceName: string): Promise<boolean> {
        this.logger.log({ msg: `Deleting workspace with ID ${workspaceName}`, func: this.deleteWorkspace });
        try {
            const doc = await this.workspaceDb.get(workspaceName);
            const result = await this.workspaceDb.remove({ _id: doc._id, _rev: doc._rev });
            if (!result.ok) {
                const msg = `Failed to delete workspace ${workspaceName}`;
                this.logger.log({ msg: msg, func: this.deleteWorkspace });
                return false;
            }
            this.logger.log({ msg: `Successfully deleted workspace with ID ${workspaceName}`, func: this.deleteWorkspace });
            return true;
        } catch (err: any) {
            const msg = `Failed to delete workspace due to error: ${err.message}`;
            this.logger.log({ msg: msg, func: this.deleteWorkspace });
            return false
        }
    }

    async getWorkspaces(skip: number, limit: number): Promise<WorkspaceModel[]> {
        this.logger.log({ msg: `Getting workspaces with limit ${limit} and skip ${skip}`, func: this.getWorkspaces });
        try {
            const result = await this.workspaceDb.allDocs({ skip: skip, limit: limit, include_docs: true });
            const docs = result.rows;
            this.logger.log({ msg: `Iterating docs of count ${docs.length}`, func: this.getWorkspaces });
            const wss: WorkspaceModel[] = docs.map((doc) => {
                const ws = doc.doc as unknown as WorkspaceModel;
                return ws;
            });
            this.logger.log({ msg: `Got workspaces ${JSON.stringify(wss)}`, func: this.getWorkspaces });
            return wss;
        } catch (err: any) {
            const msg = `Failed to get workspaces due to error: ${err.message}`;
            this.logger.log({ msg: msg, func: this.getWorkspaces });
            throw new Error(msg);
        }
    }
}