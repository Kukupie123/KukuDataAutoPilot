import { Request, Response } from "express";
import { IController } from "./IController";
import { WorkspaceModel } from "../models/WorkspaceModel";
import { WorkspaceFactory } from "../services/workspace/factory/WorkspaceServiceFactory";
import { WorkspaceService } from "../services/workspace/WorkspaceService";
//TODO: Hide this from other files
export class WorkspaceController implements IController {
    private workspaceService?: WorkspaceService;

    async initController(): Promise<void> {
        console.log("Initializing Workspace Controller")
        this.workspaceService = await WorkspaceFactory.Build();
        console.log("Initialized Workspace Controller")
    }
    async foo(req: Request, res: Response): Promise<void> {
        res.send("FOO")
    }

    async createWorkspace(req: Request, res: Response): Promise<void> {
        if (!this.workspaceService) {
            throw new Error("Workspace Service has not been initialized");
            res.json({ msg: "Workspace service has not been initialized" });
            res.statusCode = 500;
            return;
        }
        const workspace = req.body as WorkspaceModel;
        if (!workspace.name) {
            console.error(`name for workspace is undefined in payload`);
            res.json({ msg: `name for workspace is undefined in payload` }) //TODO: Create a proper function for returning
            return;
        }
        const addedWorkspace = await this.workspaceService.createWorkspace(workspace);
        if (addedWorkspace)
            res.json({ msg: `success`, data: addedWorkspace });
        else {
            res.json({ msg: `failed to add workspace` })
            res.statusCode = 500;
        }
    }
}