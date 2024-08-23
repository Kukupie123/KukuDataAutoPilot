import { RecordService } from "../services/record/RecordService";
import { IController } from "./IController";
import { ServiceManager } from "../services/ServiceManager";
import { KDAPLogger } from "../util/KDAPLogger";
import { IRecordAttributeInfo, RecordModel } from "../models/RecordModel";

export class RecordController implements IController {
    private logger = new KDAPLogger(RecordController.name);
    private recService!: RecordService;
    async initController(): Promise<void> {
        await ServiceManager.Register(RecordService);
        this.recService = ServiceManager.GetService(RecordService)
    }

    async addRecord(name: string, attributes: Map<string, IRecordAttributeInfo>, desc?: string): Promise<boolean> {
        this.logger.log({ msg: `Adding Record ${name}, with attributes ${JSON.stringify(Array.from(attributes))}`, func: this.addRecord })
        return await this.recService.addRecord(name, attributes);
    }

    async getRecord(recID: string): Promise<RecordModel | undefined> {
        this.logger.log({ msg: `Getting record ${recID}`, func: this.getRecord });
        return await this.recService.getRecord(recID);
    }

    async deleteRecord(recID: string): Promise<boolean> {
        this.logger.log({ msg: `Deleting record ${recID}`, func: this.deleteRecord });
        return await this.recService.deleteRecord(recID);
    }

    async getRecords(skip: number, limit: number): Promise<RecordModel[]> {
        this.logger.log({ msg: `Getting ALL records with skip ${skip}, limit : ${limit} `, func: this.getRecord });
        return await this.recService.getRecords(skip, limit);
    }
}