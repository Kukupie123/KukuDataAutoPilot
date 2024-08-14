import express from 'express';
import { WorkspaceRoute } from "./routes/WorkspaceRoute";
import { Workspace as WorkspaceEndpoint } from "./util/RouteEndpoints";
import { ControllerFactory } from './controllers/ControllerFactory';
import { WorkspaceController } from './controllers/WorkspaceController';



// Start the server



const startServer = async () => {
    const app = express();
    app.use(express.json()); // Middleware to parse JSON bodies

    const router = express.Router();
    //Workspace route
    const workspaceController = await ControllerFactory.BUILD(WorkspaceController);
    let rootRoute = WorkspaceEndpoint;
    router.post(rootRoute, workspaceController.foo);
    router.get(rootRoute + '/:id', workspaceController.foo); // Get Workspace
    router.put(rootRoute + '/:id', workspaceController.foo); // Update Workspace
    router.delete(rootRoute + '/:id', workspaceController.foo); // Delete Workspace
    router.get(rootRoute + '/', workspaceController.foo); // List Workspaces

    //Start
    const PORT = 3000;
    app.listen(PORT, () => {
        console.log(`Server is running on port ${PORT}`);
    });
}

startServer();