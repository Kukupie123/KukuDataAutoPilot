import { IController } from "../IController";
import { BuildOption } from "./BuildOptions";
export class ControllerFactory {
    public static async Build<T extends IController>(controllerClass: { new(...args: any[]): T }, option?: BuildOption): Promise<T> {
        console.log("Building Controller of class " + controllerClass.name)
        const controller = new controllerClass();
        await controller.initController();
        return controller;
    }
}