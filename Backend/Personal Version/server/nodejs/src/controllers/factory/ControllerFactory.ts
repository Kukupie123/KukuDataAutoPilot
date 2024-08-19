import { IController } from "../IController";
import { BuildOption } from "./BuildOptions";
import { KDAPLogger } from "../../util/KDAPLogger";
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
