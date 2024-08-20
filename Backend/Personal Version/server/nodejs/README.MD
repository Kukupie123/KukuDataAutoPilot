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

### Event System
KDAP is developed with Event driven arhictecture in mind to have loose coupling and modularity. <br>
Node's inbuilt ```eventEmitter``` is good but it is error prone due to using strings and dynamic types as ```emitter.emit('onTestEvent',somePayload)```, this may cause hard to notice bugs during refactorings and other operations as strings and dynamic payload will not throw compilation error. <br> To combat this we use a wrapper event system which utilizes the power of "TS" to enforce static types for both the event and the payload of the event. <br>
``` typescript
// Base class for custom events
export abstract class CustomEvent<T> {
    constructor(
        public readonly eventName: string,
        public readonly payload: T
    ) { }
}
```
``` typescript
// Event management class
export class EventManager {
    private static emitter: EventEmitter = new EventEmitter();

    /**
     * Emits an event with a specified class and payload.
     * @param eventClass - The class of the event to emit.
     * @param payload - The payload to pass to the event.
     */
    public static emit<T>(
        event: CustomEvent<T>): void {
        this.emitter.emit(event.eventName, event.payload);
    }

    /**
     * Registers a listener for a specific event type.
     * @param eventClass - The class of the event to listen for.
     * @param callback - The function to handle the event's payload.
     */
    public static on<T>(
        eventClass: { new(payload: T): CustomEvent<T> },
        callback: (payload: T) => void
    ): void {
        const eventName = new eventClass(null as unknown as T).eventName;
        this.emitter.addListener(eventName, (payload: T) => {
            let payloadString: string = payload as string;
            try {
                payloadString = JSON.stringify(payload);
            }
            catch (err) {
            }
            this.logger.log({ msg: `${eventName} Dispatched with payload ${payloadString}`, func: this.on });
            callback(payload);
        });
    }

    /**
     * Removes a listener for a specific event type.
     * @param eventClass - The class of the event to stop listening for.
     * @param callback - The function to remove.
     */
    public static off<T>(
        eventClass: { new(payload: T): CustomEvent<T> },
        callback: (payload: T) => void
    ): void {
        const eventName = new eventClass(null as unknown as T).eventName;
        this.emitter.removeListener(eventName, callback);
    }
}
```
Example :-
``` typescript
class TestEvent extends CustomEvent<string> {
            constructor(payload: string) {
                super("TEST_EVENT", payload);
            }
        }
        const payload = "payload stuff";
        // Setup event listener
        EventManager.on(TestEvent, (p) => {
            //Compilation error if we do not supply custom event and the correct payload type
            console.log("Test event triggered with payload " + p);
        });
        // Emit the event
        EventManager.emit(new TestEvent(payload));
```
### KDAP Logger
KDAP Logger is a simpe logger built for verbose and flexible logging . <br>
It allows devs to create new "Log Type" types easily and use them for categorising logs. Using types for logging allows us to detect mistakes at compile time which may otherwise go unnoticed if using no types. <br>
It logs the className, function name, log message, Log Type and the timestamp. <br>
It also creates appropriate files for all the different log types. <br>
Example :-
``` typescript
 async getWorkspace(id: string): Promise<WorkspaceModel | undefined> {
        const ws = dummyWS;
        this.logger.log({ msg: `Workspace retreived`, func: this.getWorkspace })
        return ws;
    }
//[WorkspaceService-1] [getWorkspaces] [Info] [2024-08-20T05:53:39.782Z] : Workspaces retreived

```