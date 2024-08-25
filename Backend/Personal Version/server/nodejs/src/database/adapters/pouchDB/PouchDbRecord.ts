import {Category} from "../../../config/kdapLogger.config";
import {IRecordAttributeInfo, RecordModel} from "../../../models/RecordModel";
import {KDAPLogger} from "../../../util/KDAPLogger";
import {validateAttribute} from "../../../util/RecordHelper";
import {RecordDTO} from "./DTO/RecordDTO";
import {PouchDb} from "./PouchDb";

export class PouchDbRecord {
    private pouchDb: PouchDb;
    private logger: KDAPLogger;

    constructor(pouchDb: PouchDb) {
        this.logger = new KDAPLogger(PouchDbRecord.name);
        this.pouchDb = pouchDb;
    }

    async addRecord(name: string, attributes: Map<string, IRecordAttributeInfo>, desc?: string): Promise<boolean> {
        this.logger.log({
            msg: `Adding record ${name} with attributes := ${JSON.stringify(Object.fromEntries(attributes))}`,
            func: this.addRecord
        });

        try {
            validateAttribute(attributes);
        } catch (err: any) {
            const msg = `Failed to validate attributes ${(err as Error).message}`;
            this.logger.log({category: Category.Error, msg: msg, func: this.addRecord});
            throw new Error(msg); // TODO: Throw exception that can be caught and sent as response
        }

        try {
            // Convert Map to object
            const attributesObject = Object.fromEntries(attributes);

            const recDTO = new RecordDTO(
                name,
                JSON.stringify(attributesObject), // Store the attributes as a JSON string of an object
                new Date(Date.now()),
                new Date(Date.now()),
                desc
            );
            await this.pouchDb.recordDB.put(recDTO);
            return true;
        } catch (err) {
            const e = err as Error;
            this.logger.log({msg: `Failed to add record due to: ${e.message}`, func: this.addRecord});
            return false;
        }
    }


    async getRecord(recID: string): Promise<RecordModel | undefined> {
        this.logger.log({msg: `Getting record with ID: ${recID}`, func: this.getRecord});

        try {
            const result = await this.pouchDb.recordDB.get<RecordDTO>(recID);

            // Log the raw DTO fetched from the database
            this.logger.log({msg: `Fetched RecordDTO: ${JSON.stringify(result)}`, func: this.getRecord});
            // Convert the attributes from JSON string back to an object
            const attributesObject = JSON.parse(result.attributes) as Record<string, IRecordAttributeInfo>;
            this.logger.log({
                msg: `AttributesObject RecordDTO: ${JSON.stringify(attributesObject)}`,
                func: this.getRecord
            });
            // Convert the object back to a Map
            const attributesMap = new Map<string, IRecordAttributeInfo>();
            for (const [key, value] of Object.entries(attributesObject)) {
                attributesMap.set(key, value);
            }
            // Create RecordModel from RecordDTO
            const recModel = new RecordModel(
                result.name,
                attributesMap,
                result.desc,
                new Date(result.created),
                new Date(result.updated)
            );
            // Log the converted RecordModel
            this.logger.log({
                msg: `Converted to RecordModel: ${JSON.stringify(recModel)} with attributes ${JSON.stringify(Object.fromEntries(attributesMap))}`,
                func: this.getRecord
            });
            return recModel;
        } catch (err) {
            this.logger.log({
                category: Category.Error,
                msg: `Failed to get record: ${(err as Error).message}`,
                func: this.getRecord
            });
            return undefined;
        }
    }

    async deleteRecord(recID: string): Promise<boolean> {
        this.logger.log({msg: `Deleting record ${recID}`, func: this.deleteRecord});
        try {
            const rec = await this.pouchDb.recordDB.get(recID);
            await this.pouchDb.recordDB.remove({_id: rec._id, _rev: rec._rev});
            return true;
        } catch (err) {
            this.logger.log({msg: `Failed to delete due to ${(err as Error).message}`, func: this.deleteRecord})
            return false;
        }
    }

    async getRecords(skip: number, limit: number): Promise<RecordModel[]> {
        try {
            this.logger.log({msg: `Getting records with skip ${skip}, limit: ${limit}`, func: this.getRecords});

            // Fetch documents from the database
            const result = await this.pouchDb.recordDB.allDocs({skip: skip, limit: limit, include_docs: true});

            return result.rows.map(doc => {
                const recDTO = doc.doc as RecordDTO;

                // Log the raw attributes JSON string
                this.logger.log({msg: `Current Attributes JSON: ${recDTO.attributes}`, func: this.getRecords});

                // Parse the attributes from the JSON string into an object
                const attributesObject = JSON.parse(recDTO.attributes) as Record<string, IRecordAttributeInfo>;

                // Convert the object to a Map
                const attributesMap = new Map<string, IRecordAttributeInfo>(Object.entries(attributesObject));
                // Create a RecordModel instance
                return new RecordModel(
                    recDTO.name,
                    attributesMap,
                    recDTO.desc,
                    new Date(recDTO.created),
                    new Date(recDTO.updated)
                );
            });
        } catch (err) {
            this.logger.log({
                category: Category.Error,
                msg: `Failed to get records: ${(err as Error).message}`,
                func: this.getRecords
            });
            throw err;
        }
    }


}
