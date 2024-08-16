import { DatabaseFactory } from "../../database/Factory/DatabaseFactory";
import { IDatabaseAdapter } from "../../database/IDatabaseAdapter";
import { RecordModel } from "../../models/RecordModel";
import { KDAPLogger } from "../../util/KDAPLogger";
import { IService } from "../IService";

export class RecordService implements IService {
    private db!: IDatabaseAdapter;
    private logger: KDAPLogger = new KDAPLogger(RecordService.name);
    async initService(): Promise<void> {
        this.db = await DatabaseFactory.Build();
    }

    async createRecord(workspaceID: string, record: RecordModel): Promise<RecordModel> {
        this.logger.log(`Creatring record : ${JSON.stringify(record)}`)
        const r: RecordModel = await this.db.createRecord(workspaceID, record);
        return r;
    }

    async getRecords(skip: number = 0, limit: number = 10): Promise<RecordModel[]> {
        this.logger.log(`'Gett records with skip : ${skip} and limit ${limit}`)
        const a = await this.db.getRecords(skip, limit);
        this.logger.log(`Get records = ${JSON.stringify(a)}`);
        return a;
    }

    /**
     * Adds the entry for the record. The fields need to match
     * @param workspaceID 
     * @param recordID 
     * @param entry 
     */
    async addEntry(workspaceID: string, recordID: string, entry: any): Promise<any> {
    }


}