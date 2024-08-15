import { IController } from "../IController";
import { BuildOption } from "./BuildOptions";
import { KDAPLogger } from "../../util/EnhancedLogger";
import { Category } from "../../config/kdapLogger.config";
export class ControllerFactory {
    private static Logger = new KDAPLogger(ControllerFactory.name);
    public static async Build<T extends IController>(
        controllerClass: { new(...args: any[]): T },
        option?: BuildOption,
        identifier?: string
    ): Promise<T> {
        this.Logger.log(Category.Controller, `Building Controller of class ${controllerClass.name}`);
        const controller = new controllerClass();
        this.Logger.log(Category.Controller, `Initializing Controller ${controllerClass.name}`);
        await controller.initController();
        this.Logger.log(Category.Controller, `Initialized Controller ${controllerClass.name}`);
        return controller;
    }
}
