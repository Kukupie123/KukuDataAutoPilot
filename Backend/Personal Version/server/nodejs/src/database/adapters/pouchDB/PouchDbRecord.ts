import { Category } from "../../../config/kdapLogger.config";
import { IRecordAttributeInfo, RecordModel } from "../../../models/RecordModel";
import { KDAPLogger } from "../../../util/KDAPLogger";
import { validateAttribute } from "../../../util/RecordHelper";

export class PouchDbRecord {
    private recordDb!: PouchDB.Database;
    private logger!: KDAPLogger;
    constructor(recordDb: PouchDB.Database) {
        this.logger = new KDAPLogger(PouchDbRecord.name);
        this.recordDb = recordDb;
    }

    async addRecord(name: string, attributes: Map<string, IRecordAttributeInfo>, desc?: string): Promise<boolean> {
        let attributesString = "";
        attributes.forEach((v, k) => {
            attributesString = `${attributesString} ${k} : ${v.attributeImportance}, ${v.attributeType}\n`
        })
        this.logger.log({ msg: `Adding record ${name} with attributes := \n${attributesString}`, func: this.addRecord })
        try {
            validateAttribute(attributes);
        }
        catch (err: any) {
            const msg = `Failed to validate attributes ${(err as Error).message}`;
            this.logger.log({ category: Category.Error, msg: msg, func: this.addRecord })
            throw new Error(msg) //TODO: Throw exception that can be caught and sent as response
        }
        const rec = new RecordModel(name, attributes, desc, new Date(Date.now()), new Date(Date.now()));
        try {
            await this.recordDb.put({ _id: rec.name, ...rec });
            return true;
        }
        catch (err) {
            const e = err as Error;
            this.logger.log({ msg: `Failed to add record due to : ${e.message}`, func: this.addRecord });
            return false;
        }
    }
    async getRecord(recID: string): Promise<RecordModel | undefined> {
        this.logger.log({ msg: `Getting record ${recID}`, func: this.getRecord })
        try {
            return await this.recordDb.get(recID) as RecordModel
        }
        catch (err) {
            this.logger.log({ msg: `Failed to get record ${(err as Error).message}`, func: this.getRecord })
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