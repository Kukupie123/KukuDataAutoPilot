import { IController } from "../IController";
import { BuildOption } from "./BuildOptions";
import { Logger as UtilLogger } from "../../util/Logger";
export class ControllerFactory {
    private static Logger = UtilLogger.CreateLogger(ControllerFactory.name);
    public static async Build<T extends IController>(
        controllerClass: { new(...args: any[]): T },
        option?: BuildOption,
        identifier?: string
    ): Promise<T> {
        this.Logger.info(`Building Controller of class ${controllerClass.name}`);
        const controller = new controllerClass();
        this.Logger.info(`Initializing Controller ${controllerClass.name}`);
        await controller.initController();
        this.Logger.info(`Initialized Controller ${controllerClass.name}`);
        return controller;
    }
}
