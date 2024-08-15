import { Request, Response } from "express";
import { IController } from "./IController";
import { WorkspaceModel } from "../models/WorkspaceModel";
import { WorkspaceService } from "../services/workspace/WorkspaceService";
import { KDAPLogger } from "../util/KDAPLogger";
import { Category } from "../config/kdapLogger.config";
import { ServiceFactory } from "../services/ServiceFactory";
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
            res.json(workspaces);
        } catch (error) {
            // Log the error and respond with an error status
            this.log.log(`Error retrieving workspaces: ${JSON.stringify(error)}`);
            res.status(500).json({ error: 'Unable to fetch workspaces' });
        }
    }
}