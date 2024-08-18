import { beforeAll, describe, expect, test } from "@jest/globals";
import { DatabaseFactory } from "../../../../src/database/Factory/DatabaseFactory";
import { PouchDb } from "../../../../src/database/adapters/pouchDB/PouchDb";
import { RecordModel } from "../../../../src/models/RecordModel";
import { IDatabaseAdapter } from "../../../../src/database/IDatabaseAdapter";

describe('Pouch Database Tests', () => {
    let db: IDatabaseAdapter;
    const docID = "TEST";
    const recID = "TEST_REC";
    beforeAll(async () => {
        db = await DatabaseFactory.Build(PouchDb);
        //await db.deleteRecord(recID, docID);
        //await db.deleteWorkspace(docID);
    })

    test('Add Workspace success test', async () => {
        /*
        const addedWS = await db.addWorkspace(docID, `${docID} DESC`);
        expect(addedWS).toBeDefined();
        expect(addedWS.name).toBe(docID);
        expect(addedWS.records).toStrictEqual([]);
        console.log(JSON.stringify(addedWS));
        */
    });

    test('Add Record Test', async () => {
        console.log("Starting Add Record Test")
        const addedWS = await db.addWorkspace(docID, `${docID} DESC`);
        const addedRec = await db.addRecord(new RecordModel(recID, [{ "_id": { importance: "important", type: "date" } }]), docID);
        expect(addedRec).toBeDefined();
    })
})
