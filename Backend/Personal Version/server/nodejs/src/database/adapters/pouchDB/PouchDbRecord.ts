import { Category } from "../../../config/kdapLogger.config";
import { IRecordAttributeInfo, RecordModel } from "../../../models/RecordModel";
import { KDAPLogger } from "../../../util/KDAPLogger";
import { validateAttribute } from "../../../util/RecordHelper";
import { RecordDTO } from "./DTO/RecordDTO";

export class PouchDbRecord {
    private recordDb!: PouchDB.Database;
    private logger!: KDAPLogger;
    constructor(recordDb: PouchDB.Database) {
        this.logger = new KDAPLogger(PouchDbRecord.name);
        this.recordDb = recordDb;
    }

    async addRecord(name: string, attributes: Map<string, IRecordAttributeInfo>, desc?: string): Promise<boolean> {
        this.logger.log({ msg: `Adding record ${name} with attributes := ${JSON.stringify(Array.from(attributes.entries()))}`, func: this.addRecord })
        try {
            validateAttribute(attributes);
        }
        catch (err: any) {
            const msg = `Failed to validate attributes ${(err as Error).message}`;
            this.logger.log({ category: Category.Error, msg: msg, func: this.addRecord })
            throw new Error(msg) //TODO: Throw exception that can be caught and sent as response
        }
        try {
            const att2 = new RecordDTO(name, JSON.stringify(Array.from(attributes)), new Date(Date.now()), new Date(Date.now()), desc);
            await this.recordDb.put(att2);
            return true;
        }
        catch (err) {
            const e = err as Error;
            this.logger.log({ msg: `Failed to add record due to : ${e.message}`, func: this.addRecord });
            return false;
        }
    }

    async getRecord(recID: string): Promise<RecordModel | undefined> {
        this.logger.log({ msg: `Getting record ${recID}`, func: this.getRecord });
        try {
            const result = await this.recordDb.get<RecordDTO>(recID);
            const recordDTO = new RecordDTO(result.name, result.attributes, new Date(result.created), new Date(result.updated), result._rev);
            this.logger.log({ msg: `Gotten record DTO :  ${JSON.stringify(recordDTO)}`, func: this.getRecord });
            const recModel = recordDTO.toRecordModel();
            this.logger.log({ msg: `Gotten record : ${JSON.stringify(recModel)} with attributes : ${JSON.stringify(Array.from(recModel.attributes.entries()))}` })
            return recModel;
        } catch (err) {
            this.logger.log({ msg: `Failed to get record ${(err as Error).message}`, func: this.getRecord });
            return undefined;
        }
    }

    async deleteRecord(recID: string): Promise<boolean> {
        this.logger.log({ msg: `Deleting record ${recID}`, func: this.deleteRecord });
        try {
            const rec = await this.recordDb.get(recID);
            await this.recordDb.remove({ _id: rec._id, _rev: rec._rev });
            return true;
        }
        catch (err) {
            this.logger.log({ msg: `Failed to delete due to ${(err as Error).message}`, func: this.deleteRecord })
            return false;
        }
    }
    async getRecords(skip: number, limit: number): Promise<RecordModel[]> {
        try {
            this.logger.log({ msg: `Getting records skip ${skip}, limit : ${limit}`, func: this.getRecords })
            const result = await this.recordDb.allDocs({ skip: skip, limit: limit });
            return result.rows.map(doc => {
                return doc as unknown as RecordModel;
            })
        }
        catch (err) {
            this.logger.log({ category: Category.Error, msg: `Failed to get records ${(err as Error).message}`, func: this.getRecords })
            throw err;
        }
    }
}