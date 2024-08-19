import { Request, Response } from "express";
import { IController } from "./IController";
import { WorkspaceModel } from "../models/WorkspaceModel";
import { WorkspaceService } from "../services/workspace/WorkspaceService";
import { KDAPLogger } from "../util/KDAPLogger";
import { ServiceFactory } from "../services/ServiceFactory";
import { sendResponse } from "../helper/ResHelper";
import { ResponseDataGeneric } from "../models/response/ResponseDataGeneric";
import { HttpStatusCode } from "../util/HttpCodes";
//TODO: Hide this from other files
//TODO: Better exception handling in the future
export class WorkspaceController implements IController {
    private log = new KDAPLogger(WorkspaceController.name);

    private workspaceService!: WorkspaceService;

    async initController(): Promise<void> {
        this.log.log({ msg: "Initializing Workspace Controller", func: this.initController })
        this.workspaceService = await ServiceFactory.Build(WorkspaceService);
        this.log.log({ msg: "Initialized Workspace Controller", func: this.initController })
    }
    async foo(req: Request, res: Response): Promise<void> {
        res.send("FOO")
    }

    async addWorkspace(req: Request, res: Response): Promise<void> {
        this.log.log({ msg: `Request to Add Workspace with payload ${JSON.stringify(req.body)}`, func: this.addWorkspace })
        const workspace = req.body as WorkspaceModel;
        if (!workspace.name) {
            throw new Error("Name for workspace is undefined in payload");
        }
        const ws = await this.workspaceService.addWorkspace(workspace.name, workspace.desc);
        if (ws)
            sendResponse(HttpStatusCode.OK, "success", new ResponseDataGeneric(ws), res);
        else {
            throw new Error("Failed to add workspace")
        }
    }

    async getWorkspace(req: Request, res: Response) {
        const id = req.params['id']
        this.log.log({ msg: `GetWorkspace with ID ${id}`, func: this.getWorkspace })
        const ws = await this.workspaceService.getWorkspace(id);
        this.log.log({ msg: `Retreived Workspace is ${JSON.stringify(ws)}`, func: this.getWorkspace })
        res.json({ msg: "success", data: ws });

    }

    async getWorkspaces(req: Request, res: Response): Promise<void> {
        const skip = parseInt(req.query['skip'] as string, 0) || 0;
        const limit = parseInt(req.query['limit'] as string, 10) || 10;
        this.log.log({ msg: `Getting workspaces list with skip=${skip} and limit=${limit}`, func: this.getWorkspaces });
        try {
            const wss = await this.workspaceService.getWorkspaces(skip, limit);
            this.log.log({ msg: `Retrieved workspaces: ${JSON.stringify(wss)}`, func: this.getWorkspaces });
            sendResponse(HttpStatusCode.OK, "Success", new ResponseDataGeneric(wss), res);
        } catch (error: any) {
            throw new Error(error.message);
        }
    }

    async deleteWorkspaces(req: Request, res: Response): Promise<void> {
        const id = req.params['id'];
        this.log.log({ msg: `Deleting workspace with ID ${id}`, func: this.deleteWorkspaces });
        this.workspaceService.deleteWorkspace(id);
        this.log.log({ msg: `Deleted workspace with id ${id}`, func: this.deleteWorkspaces });
    }
}