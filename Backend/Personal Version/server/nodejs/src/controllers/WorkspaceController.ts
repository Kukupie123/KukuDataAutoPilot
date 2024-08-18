import { Request, Response } from "express";
import { IController } from "./IController";
import { WorkspaceModel } from "../models/WorkspaceModel";
import { WorkspaceService } from "../services/workspace/WorkspaceService";
import { KDAPLogger } from "../util/KDAPLogger";
import { ServiceFactory } from "../services/ServiceFactory";
import { ResponseException } from "../models/exception/ResponseException";
import { sendResponse } from "../helper/ResHelper";
import { ResponseDataGeneric } from "../models/response/ResponseDataGeneric";
import { HttpStatusCode } from "../util/HttpCodes";
//TODO: Hide this from other files
//TODO: Better exception handling in the future
export class WorkspaceController implements IController {
    private log = new KDAPLogger(WorkspaceController.name);

    private workspaceService!: WorkspaceService;

    async initController(): Promise<void> {
        this.log.log("Initializing Workspace Controller")
        this.workspaceService = await ServiceFactory.Build(WorkspaceService);
        this.log.log("Initialized Workspace Controller")
    }
    async foo(req: Request, res: Response): Promise<void> {
        res.send("FOO")
    }

    async addWorkspace(req: Request, res: Response): Promise<void> {
        this.log.log(`Request to Add Workspace with payload ${JSON.stringify(req.body)}`)
        const workspace = req.body as WorkspaceModel;
        if (!workspace.name) {
            throw new ResponseException("Name for workspace is undefined in payload", HttpStatusCode.BAD_REQUEST);
        }
        const ws = await this.workspaceService.addWorkspace(workspace.name, workspace.desc);
        if (ws)
            sendResponse(HttpStatusCode.OK, "success", new ResponseDataGeneric<WorkspaceModel>(ws), res);
        else {
            throw new ResponseException("Failed to add workspace", 500)
        }
    }

    async getWorkspace(req: Request, res: Response) {
        const id = req.params['id']
        this.log.log(`GetWorkspace with ID ${id}`)
        const ws = await this.workspaceService.getWorkspace(id);
        this.log.log(`Retreived Workspace is ${JSON.stringify(ws)}`)
        res.json({ msg: "success", data: ws });

    }

    async getWorkspaces(req: Request, res: Response): Promise<void> {
        const skip = parseInt(req.query['skip'] as string, 0) || 0;
        const limit = parseInt(req.query['limit'] as string, 10) || 10;
        this.log.log(`Getting workspaces list with skip=${skip} and limit=${limit}`);
        try {
            const wss = await this.workspaceService.getWorkspaces(skip, limit);
            this.log.log(`Retrieved workspaces: ${JSON.stringify(wss)}`);
            sendResponse(HttpStatusCode.OK, "Success", new ResponseDataGeneric(wss), res);
        } catch (error) {
            throw new ResponseException(JSON.stringify(error), 500);
        }
    }

    async deleteWorkspaces(req: Request, res: Response): Promise<void> {
        const id = req.params['id'];
        this.log.log(`Deleting workspace with ID ${id}`);
        this.workspaceService.deleteWorkspace(id);
        this.log.log(`Deleted workspace with id ${id}`);
    }
}