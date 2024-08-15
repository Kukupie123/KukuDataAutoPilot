import { IService } from "./IService";

export class ServiceFactory {
    public static async Build<T extends IService>(serviceClass: { new(...args: any[]): T }) {
        const service = new serviceClass();
        await service.initService();
        return service;
    }
}