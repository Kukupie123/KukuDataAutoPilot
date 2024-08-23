import { DatabaseFactory } from "../../database/Factory/DatabaseFactory";
import { IDatabaseAdapter } from "../../database/interface/IDatabaseAdapter";
import { IRecordAttributeInfo, RecordModel } from "../../models/RecordModel";
import { KDAPLogger } from "../../util/KDAPLogger";
import { IService } from "../IService";

export class RecordService implements IService {
    private db!: IDatabaseAdapter;
    private logger: KDAPLogger = new KDAPLogger(RecordService.name);
    async initService(): Promise<void> {
        this.db = await DatabaseFactory.Build();
    }

    async addRecord(name: string, attributes: Map<string, IRecordAttributeInfo>, desc?: string): Promise<boolean> {
        this.logger.log({ msg: `Adding Record ${name}, with attributes ${JSON.stringify(Array.from(attributes))}`, func: this.addRecord })
        return await this.db.addRecord(name, attributes, desc);
    }

    async getRecord(recID: string): Promise<RecordModel | undefined> {
        this.logger.log({ msg: `Getting record ${recID}`, func: this.getRecord });
        return await this.db.getRecord(recID);
    }

    async deleteRecord(recID: string): Promise<boolean> {
        this.logger.log({ msg: `Deleting record ${recID}`, func: this.deleteRecord });
        return await this.db.deleteRecord(recID);
    }

    async getRecords(skip: number, limit: number): Promise<RecordModel[]> {
        this.logger.log({ msg: `Getting ALL records with skip ${skip}, limit : ${limit} `, func: this.getRecord });
        return await this.db.getRecords(skip, limit);
    }




}