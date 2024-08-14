import { Request, Response } from 'express';
import { Workspace } from '../services/database/models/Workspace';
export class WorkspaceController {
    async createWorkspace(req: Request, res: Response) {
        const workspace = req.body as Workspace;
        if (!workspace.name) {
            res.statusCode = 400;
            res.json({ msg: `"name" is missing from payload` });
            return;
        }
        res.json({
            name: workspace.name
        })
    }
}


