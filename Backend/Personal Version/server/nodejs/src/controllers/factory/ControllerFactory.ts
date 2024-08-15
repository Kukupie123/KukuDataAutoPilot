import { IController } from "../IController";
import { BuildOption } from "./BuildOptions";
import { KDAPLogger } from "../../util/KDAPLogger";
import { Category } from "../../config/kdapLogger.config";
export class ControllerFactory {
    private static Logger = new KDAPLogger(ControllerFactory.name, Category.Controller);
    public static async Build<T extends IController>(
        controllerClass: { new(...args: any[]): T },
        option?: BuildOption,
    ): Promise<T> {
        this.Logger.log(`Building Controller of class ${controllerClass.name}`);
        const controller = new controllerClass();
        this.Logger.log(`Initializing Controller ${controllerClass.name}`);
        await controller.initController();
        this.Logger.log(`Initialized Controller ${controllerClass.name}`);
        return controller;
    }
}
