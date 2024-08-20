# DESIGN DOC
The entry point of the application is "main.ts". Upon viewing "main.ts" we can find a couple of components.
## Components
### RouteBuilder
Route builder is a component created for easy creation and management of Routes. <br>
It works by implementing a ```IKDAPRoute```.
``` typescript
import { IKDAPRoute, RouteDefinition } from "./interface/IKDAPRoute";
import { WorkspaceController } from "../controllers/WorkspaceController"; // Adjust the import path as needed
import { ControllerFactory } from "../controllers/factory/ControllerFactory";

export class WorkspaceRoute implements IKDAPRoute {
    async initRouter(): Promise<void> {
    }
    getRoutes(): RouteDefinition[] {
        return [
            {
                path: this.rootRoute,
                method: "post",
                handler: this.workspaceController.addWorkspace.bind(this.workspaceController)
            },
            //...other routes
        ];
    }
}

```
Example :- 
``` typescript
    const router = express.Router();
    await buildRoute(router, WorkspaceRoute);
```
### Factory
Factory is used to build several part of the application. It has a Build function which initializes and returns a valid object.
``` typescript
export interface IController {
    /**
     * Called by ControllerFactory during build
     */
    initController(): Promise<void>;
}
```


``` typescript
export class ControllerFactory {
    private static Logger = new KDAPLogger(ControllerFactory.name);
    public static async Build<T extends IController>(
        controllerClass: { new(...args: any[]): T },
        option?: BuildOption,
    ): Promise<T> {
        this.Logger.log({ msg: `Building Controller of class ${controllerClass.name}`, func: this.Build });
        const controller = new controllerClass();
        this.Logger.log({ msg: `Initializing Controller ${controllerClass.name}`, func: this.Build });
        await controller.initController();
        this.Logger.log({ msg: `Initialized Controller ${controllerClass.name}`, func: this.Build });
        return controller;
    }
}
```
Example :-
``` typescript
workspaceController = await ControllerFactory.Build(WorkspaceController);
```
# Manager
Managers handle creating instances and providing them when required.
``` typescript
export interface IService {
    initService(): Promise<void>;
}
```

``` typescript
import { Category } from "../config/kdapLogger.config";
import { KDAPLogger } from "../util/KDAPLogger";
import { IService } from "./IService";

export class ServiceManager {
    private static log = new KDAPLogger(ServiceManager.name);
    private static services: Map<string, IService> = new Map();

    public static async Register<T extends IService>(serviceClass: { new(...args: any[]): T }) {
        const name = serviceClass.name;
        if (this.services.has(name)) {
            this.log.log({ msg: `Service ${name} already exists.`, func: this.Register })
            return;
        }
        const instance: IService = new serviceClass();
        await instance.initService();
        this.services.set(name, instance);
        this.log.log({ msg: `Service ${name} Registered` });
    }

    public static GetService<T extends IService>(serviceClass: { new(...args: any[]): T }): T {
        if (!this.services.has(serviceClass.name)) {
            const msg = `Service ${serviceClass.name} has not been registered!`;
            this.log.log({ msg: msg, category: Category.Error, func: this.GetService })
            throw new Error(msg)
        }
        return this.services.get(serviceClass.name) as T;
    }
}
```
Example :-
``` typescript
await ServiceManager.Register(WorkspaceService);
workspaceService = ServiceManager.GetService(WorkspaceService);
```