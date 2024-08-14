import { Request, Response } from "express";
import { DatabaseFactory } from "../database/Factory/DatabaseFactory";
import { IController } from "./IController";
import { WorkspaceModel } from "../models/WorkspaceModel";
import { WorkspaceService } from "../services/workspace/WorkspaceService";
import { WorkspaceFactory } from "../services/workspace/factory/WorkspaceServiceFactory";
//TODO: Hide this from other files
export class WorkspaceController implements IController {
    private workspaceService!: WorkspaceService;

    async initController(): Promise<void> {
        this.workspaceService = await WorkspaceFactory.Build();
    }
    async foo(req: Request, res: Response): Promise<void> {
        res.send("FOO")
    }

    async createWorkspace(req: Request, res: Response): Promise<void> {
        const workspace = req.body as WorkspaceModel;
        if (!workspace.name) {
            console.error(`name for workspace is undefined in payload`);
            res.json({ msg: `name for workspace is undefined in payload` }) //TODO: Create a proper function for returning
            return;
        }

    }
}