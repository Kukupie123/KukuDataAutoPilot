import { RecordController } from "../controllers/RecordController";
import { ControllerFactory } from "../controllers/factory/ControllerFactory";
import { IKDAPRoute, RouteDefinition } from "./interface/IKDAPRoute";

export class RecordRoute implements IKDAPRoute {
    private recordController!: RecordController;
    private rootPath = "/record"

    async initRouter(): Promise<void> {
        this.recordController = await ControllerFactory.Build(RecordController);
    }

    getRoutes(): RouteDefinition[] {
        return [
            {
                method: "post",
                path: `${this.rootPath}`,
                handler: this.recordController.addRecord.bind(this.recordController)
            },
            {
                method: "get",
                path: `${this.rootPath}`,
                handler: this.recordController.getRecords.bind(this.recordController)
            },
        ];
    }

}