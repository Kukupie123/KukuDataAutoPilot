import express from 'express';
import { ControllerFactory } from './controllers/factory/ControllerFactory';
import { WorkspaceController } from './controllers/WorkspaceController';
import { KDAPLogger } from './util/EnhancedLogger';

const startServer = async () => {
    const logger = new KDAPLogger("MAIN");
    const app = express();
    app.use(express.json()); // Middleware to parse JSON bodies

    // Routes initialization
    const router = express.Router();

    /*
    The Factory design pattern is used to create instances of controllers.
    It abstracts the instantiation process, allowing for centralized management of controller creation,
    including any required initialization or dependency injection, ensuring that the controllers are ready to use.
    */
    try {
        const workspaceController = await ControllerFactory.Build<WorkspaceController>(WorkspaceController);

        let rootRoute = "/workspaces";
        router.post(rootRoute, workspaceController.createWorkspace.bind(workspaceController)); // Create Workspace
        router.get(rootRoute + '/:id', workspaceController.getWorkspace.bind(workspaceController)); // Get Workspace
        router.put(rootRoute + '/:id', workspaceController.foo.bind(workspaceController)); // Update Workspace
        router.delete(rootRoute + '/:id', workspaceController.foo.bind(workspaceController)); // Delete Workspace
        router.get(rootRoute, workspaceController.getWorkspaces.bind(workspaceController)); // List Workspaces

        app.use('/api', router);

        // Start
        const PORT = 3000;
        app.listen(PORT, () => {
            console.log(`Server is running on port ${PORT}`);
        });

    } catch (error) {
        console.error('Failed to initialize server:', error);
        process.exit(1); // Exit the process with an error code
    }

    // Global error handler
    app.use((err: any, req: express.Request, res: express.Response, next: express.NextFunction) => {
        console.error('Unhandled error:', err);
        res.status(500).json({ message: 'Internal Server Error' });
    });
}

startServer();
/**
 * 
 * 
 * 
 * 
 * 
 * {
 * "project name"
 * "workspace ID",
 * "structure(JSON)"
 * }
 * 
 * FOr structures we can have these primitive datatypes
 * int, float, text, date
 * 
 * 
 * What happens when we add a record to a workspace?
 * add the record in records table along with the structure
 * add the link in connector table for fast access
 * create a dynamic table TBD**********
 * especially how to store them. As folders?
 * 
 * lets say i add a record "inventory" in workspace "shop"
 * 
 * i add it in records, connectors, structure table
 * Then i go to user_tables/shop/inventory.table
 * Sounds good right?
 */