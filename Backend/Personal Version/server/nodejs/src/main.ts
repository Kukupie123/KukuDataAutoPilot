import express from 'express';
import { ControllerFactory } from './controllers/factory/ControllerFactory';
import { WorkspaceController } from './controllers/WorkspaceController';
import { KDAPLogger } from './util/KDAPLogger';
import { RecordController } from './controllers/RecordController';

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

        let rootRoute = "/workspace";
        router.post(rootRoute, workspaceController.createWorkspace.bind(workspaceController)); // Create Workspace
        router.get(rootRoute + '/:id', workspaceController.getWorkspace.bind(workspaceController)); // Get Workspace
        router.put(rootRoute + '/:id', workspaceController.foo.bind(workspaceController)); // Update Workspace
        router.delete(rootRoute + '/:id', workspaceController.foo.bind(workspaceController)); // Delete Workspace
        router.get(rootRoute, workspaceController.getWorkspaces.bind(workspaceController)); // List Workspaces

        const recordController = await ControllerFactory.Build(RecordController);
        rootRoute = "/project";
        router.post(rootRoute, recordController.createRecord.bind(recordController)); //Create new record. Send workspaceID as query 
        router.get(rootRoute, recordController.getRecords.bind(recordController)); //Get all records

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
//TODO: Use event driven architecture by making functions very small by breaking them down and then setup listeners to use each functions.

//TODO: Fix updating workspace record attrivute not working  and creating dynamic table at the end

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
 * What happens when we create record to a workspace?
 * 1. create a new record entry
 * 2. go to the workspace table and update the record
 * 3. create the dynamic record based on attributes provided in "user-defined-tables/workspace-name" directory.
 * 4. Task done
 * 
 * lets define the structure json now. Its simply a name vs type map
 * 
 * {
 * "name":"text",
 * "age":"int"
 * "DOB":"date",
 * "weight" :"float"
 * }
 * 
 * 
 */