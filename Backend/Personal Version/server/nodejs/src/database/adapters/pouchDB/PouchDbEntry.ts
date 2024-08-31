import {IRecordEntryAdapter} from "../../interface/IRecordEntryAdapter";
import {PouchDb} from "./PouchDb";
import {KDAPLogger} from "../../../util/KDAPLogger";

export class PouchDbEntry implements IRecordEntryAdapter {
    private pouch: PouchDb;
    private log = new KDAPLogger(PouchDbEntry.name);

    constructor(pouchDb: PouchDb) {
        this.pouch = pouchDb;
    }

    async addEntryToRecord(recordID: string, entryNameValue: Map<string, any>): Promise<boolean> {
    }

    async removeEntryFromRecord(recordID: string, entryID: string): Promise<boolean> {
    }

    async editEntryFromRecord(recordID: string, entryNameValue: Map<string, any>): Promise<boolean> {
    }

}
