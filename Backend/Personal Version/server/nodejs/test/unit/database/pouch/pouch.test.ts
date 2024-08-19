import { beforeEach, afterEach, describe, expect, it, test } from "@jest/globals";
import { PouchDb } from "../../../../src/database/adapters/pouchDB/PouchDb";
import { KDAPLogger } from "../../../../src/util/KDAPLogger";
import { WorkspaceModel } from "../../../../src/models/WorkspaceModel";
import { skip } from "node:test";

describe("Pouch DB Tests", () => {
    const log = new KDAPLogger("Pouch DB Tests");
    let db: PouchDb;
    const testWorkspaceName = "TEST_WS";
    const testWorkspaceDesc = "TEST_WS_DESC";
    const range = 100;

    beforeEach(async () => {
        db = new PouchDb();
        await db.init();
    });

    afterEach(async () => {
        await db.deleteWorkspace(testWorkspaceName);
        for (let i = 0; i < range; i++) {
            await deleteWs(i.toString());
        }
    });

    const addWs = async (optionalName?: string) => {
        if (optionalName) {
            return await db.addWorkspace(optionalName);
        }
        return await db.addWorkspace(testWorkspaceName, testWorkspaceDesc);
    }

    const updateWs = async (ws: WorkspaceModel) => {
        return await db.updateWorkspace(ws);
    }

    const getWs = async (optionalWorkspaceID: string = "") => {
        if (optionalWorkspaceID === "")
            return await db.getWorkspace(testWorkspaceName);
        return await db.getWorkspace(optionalWorkspaceID);
    }

    const deleteWs = async (optionalWorkspaceID: string = "") => {
        if (optionalWorkspaceID === "")
            return await db.deleteWorkspace(testWorkspaceName)
        return await db.deleteWorkspace(optionalWorkspaceID);
    }

    test("Add Workspace Tests", async () => {
        //Simple add test
        const ws = await addWs();
        expect(ws).toBeDefined();
        expect(ws.name).toBe(testWorkspaceName)
        expect(ws.desc).toBe(testWorkspaceDesc);

        //Adding already exisiting workspace test
        try {
            const ws2 = await addWs();
            expect(true).toBe(false); //Fail
        }
        catch (err) {
            //If it throws exception saying duplicate. Test passed
            expect(true).toBe(true);
        }
    });

    test("Get Workspace Tests", async () => {
        //Simple get test after adding ws
        await addWs();
        const ws = await getWs();
        expect(ws).toBeDefined();
        expect(ws!.name).toBe(testWorkspaceName)
        expect(ws!.desc).toBe(testWorkspaceDesc);

        //Simple get test on non existing ws
        const ws2 = await getWs("non-existing-ws");
        expect(ws2).toBeUndefined();
    })

    test("Delete Workspace Tests", async () => {
        //Simple delete test
        await addWs();
        const result = await deleteWs();
        expect(result).toBe(true);

        //Delete non existing ws
        const result2 = await deleteWs("non-existing-ws")
        expect(result2).toBe(false);
    })

    test("Update Workspace Tests", async () => {
        let ws = await addWs();
        ws.desc = "UPDATED DESC";
        const result = await updateWs(ws);
        expect(result).toBe(true);

        //updating non existing ws
        const result2 = await updateWs(new WorkspaceModel("non-existing", 'desc'))
        expect(result2).toBe(false);
    })

    test("Get Workspaces test", async () => {
        //Simpple zero gets
        let wss = await db.getWorkspaces(0, range);
        expect(wss.length).toBe(0);

        //Simple single get
        await addWs();
        wss = await db.getWorkspaces(0, range);
        expect(wss.length).toBe(1);
        await deleteWs();

        //Looped tests
        for (let i = 0; i < range; i++) {
            await addWs(i.toString());
        }

        wss = await db.getWorkspaces(0, range);
        expect(wss.length).toBe(range);
    })
});
