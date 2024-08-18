import { beforeAll, describe, expect, test, afterEach } from "@jest/globals";
import { PouchDb } from "../../../../src/database/adapters/PouchDb";

describe('Workspace tests', () => {
    const docID = "TEST"
    const db = new PouchDb();

    beforeAll(async () => {
        console.log("Initializing TEST PouchDB")
        await db.init();
    });

    // Delete the added record after each test
    afterEach(async () => {
        console.log(`Deleting ${docID} workspace`);
        await db.deleteWorkspace(docID);
    });

    test('Add Workspace success test', async () => {
        const addedWS = await db.addWorkspace(docID, `${docID} DESC`);
        expect(addedWS).toBeDefined();
        expect(addedWS.name).toBe(docID);
    });
});
