import express from 'express';
import { ControllerFactory } from './controllers/factory/ControllerFactory';
import { WorkspaceController } from './controllers/WorkspaceController';

const startServer = async () => {
    const app = express();
    app.use(express.json()); // Middleware to parse JSON bodies

    // Routes initialization
    const router = express.Router();

    /*
    The Factory design pattern is used to create instances of controllers.
    It abstracts the instantiation process, allowing for centralized management of controller creation,
    including any required initialization or dependency injection, ensuring that the controllers are ready to use.
    */
    const workspaceController = await ControllerFactory.Build<WorkspaceController>(WorkspaceController);

    let rootRoute = "/workspaces";
    router.post(rootRoute, workspaceController.foo); // Create Workspace
    router.get(rootRoute + '/:id', workspaceController.foo); // Get Workspace
    router.put(rootRoute + '/:id', workspaceController.foo); // Update Workspace
    router.delete(rootRoute + '/:id', workspaceController.foo); // Delete Workspace
    router.get(rootRoute, workspaceController.foo); // List Workspaces

    app.use('/api', router);

    // Start
    const PORT = 3000;
    app.listen(PORT, () => {
        console.log(`Server is running on port ${PORT}`);
    });
}

startServer();
