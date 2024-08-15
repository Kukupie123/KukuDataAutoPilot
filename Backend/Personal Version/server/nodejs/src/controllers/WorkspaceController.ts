import { Request, Response } from "express";
import { IController } from "./IController";
import { WorkspaceModel } from "../models/WorkspaceModel";
import { WorkspaceFactory } from "../services/workspace/factory/WorkspaceServiceFactory";
import { WorkspaceService } from "../services/workspace/WorkspaceService";
import { KDAPLogger } from "../util/EnhancedLogger";
import { Category } from "../config/kdapLogger.config";
//TODO: Hide this from other files
export class WorkspaceController implements IController {
    private logger = new KDAPLogger(WorkspaceController.name, Category.Controller);
    private workspaceService!: WorkspaceService;

    async initController(): Promise<void> {
        this.logger.log("Initializing Workspace Controller")
        this.workspaceService = await WorkspaceFactory.Build();
        this.logger.log("Initialized Workspace Controller")
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

    async getWorkspaces(req: Request, res: Response): Promise<void> {
        // Parse and validate the query parameters
        const skip = parseInt(req.query['skip'] as string, 0) || 0;
        const limit = parseInt(req.query['limit'] as string, 10) || 10;

        // Log the start of the operation
        this.logger.log(`Getting workspaces list with skip=${skip} and limit=${limit}`);

        try {
            // Fetch workspaces from the service
            const workspaces = await this.workspaceService.getWorkspaces(skip, limit);

            // Log the retrieved workspaces
            this.logger.log(`Retrieved workspaces: ${JSON.stringify(workspaces)}`);

            // Send the workspaces as JSON response
            res.json(workspaces);
        } catch (error) {
            // Log the error and respond with an error status
            this.logger.log(`Error retrieving workspaces: ${JSON.stringify(error)}`);
            res.status(500).json({ error: 'Unable to fetch workspaces' });
        }
    }
}