import express from 'express';
import { WorkspaceController } from "../controllers/WorkspaceController";
export class WorkspaceRoute {
    router: express.Router;
    workspaceController: WorkspaceController;
    constructor() {
        this.workspaceController = new WorkspaceController();
        this.router = express.Router();
        this.router.post("/", this.workspaceController.createWorkspace);
        this.router.get('/:id', () => { }); // Get Workspace
        this.router.put('/:id', () => { }); // Update Workspace
        this.router.delete('/:id', () => { }); // Delete Workspace
        this.router.get('/', () => { }); // List Workspaces 
    }
}