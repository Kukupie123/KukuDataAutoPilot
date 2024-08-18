import { IKDAPRoute, RouteDefinition } from "./interface/IKDAPRoute";
import { WorkspaceController } from "../controllers/WorkspaceController"; // Adjust the import path as needed
import { ControllerFactory } from "../controllers/factory/ControllerFactory";

export class WorkspaceRoute implements IKDAPRoute {
    private rootRoute = "/workspace";
    private workspaceController!: WorkspaceController;

    async initRouter(): Promise<void> {
        this.workspaceController = await ControllerFactory.Build(WorkspaceController);
    }

    getRoutes(): RouteDefinition[] {
        return [
            {
                path: this.rootRoute,
                method: "post",
                handler: this.workspaceController.addWorkspace.bind(this.workspaceController)
            },
            {
                path: `${this.rootRoute}/:id`,
                method: "get",
                handler: this.workspaceController.getWorkspace.bind(this.workspaceController)
            },
            {
                path: `${this.rootRoute}/:id`,
                method: "put",
                handler: this.workspaceController.foo.bind(this.workspaceController)
            },
            {
                path: `${this.rootRoute}/:id`,
                method: "delete",
                handler: this.workspaceController.foo.bind(this.workspaceController)
            },
            {
                path: this.rootRoute,
                method: "get",
                handler: this.workspaceController.getWorkspaces.bind(this.workspaceController)
            }
        ];
    }
}
