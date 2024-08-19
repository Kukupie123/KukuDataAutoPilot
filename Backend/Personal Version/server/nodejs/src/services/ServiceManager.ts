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