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



}