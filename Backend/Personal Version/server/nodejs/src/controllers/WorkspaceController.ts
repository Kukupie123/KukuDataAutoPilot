import { Request, Response } from "express";
import { IController } from "./IController";
import { WorkspaceModel } from "../models/WorkspaceModel";
import { WorkspaceService } from "../services/workspace/WorkspaceService";
import { KDAPLogger } from "../util/KDAPLogger";
import { Category } from "../config/kdapLogger.config";
import { ServiceFactory } from "../services/ServiceFactory";
import { ResponseException } from "../models/exception/ResponseException";
import { sendResponse } from "../helper/ResHelper";
import { ResponseDataGeneric } from "../models/response/ResponseDataGeneric";
import { HttpStatusCode } from "../util/HttpCodes";
//TODO: Hide this from other files
//TODO: Better exception handling in the future
export class WorkspaceController implements IController {
    private log = new KDAPLogger(WorkspaceController.name, Category.Controller);

    private workspaceService!: WorkspaceService;

    async initController(): Promise<void> {
        this.log.log("Initializing Workspace Controller")
        this.workspaceService = await ServiceFactory.Build(WorkspaceService);
        this.log.log("Initialized Workspace Controller")
    }
    async foo(req: Request, res: Response): Promise<void> {
        res.send("FOO")
    }

    async createWorkspace(req: Request, res: Response): Promise<void> {

        const workspace = req.body as WorkspaceModel;
        if (!workspace.name) {
            throw new ResponseException("Name for workspace is undefined in payload", 405);
        }
        const addedWorkspace = await this.workspaceService.createWorkspace(workspace);
        if (addedWorkspace)
            sendResponse(200, "success", new ResponseDataGeneric<WorkspaceModel>(addedWorkspace), res);
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
        // Parse and validate the query parameters
        const skip = parseInt(req.query['skip'] as string, 0) || 0;
        const limit = parseInt(req.query['limit'] as string, 10) || 10;

        // Log the start of the operation
        this.log.log(`Getting workspaces list with skip=${skip} and limit=${limit}`);

        try {
            // Fetch workspaces from the service
            const workspaces = await this.workspaceService.getWorkspaces(skip, limit);

            // Log the retrieved workspaces
            this.log.log(`Retrieved workspaces: ${JSON.stringify(workspaces)}`);

            // Send the workspaces as JSON response
            sendResponse(HttpStatusCode.OK, "Success", new ResponseDataGeneric(workspaces), res);
        } catch (error) {
            throw new ResponseException(JSON.stringify(error), 500);
        }
    }
}