import {afterEach, beforeEach, describe, expect as e, test} from "@jest/globals";
import {PouchDb} from "../../../../src/database/adapters/pouchDB/PouchDb";
import {KDAPLogger} from "../../../../src/util/KDAPLogger";
import {WorkspaceModel} from "../../../../src/models/WorkspaceModel";
import {
    IRecordAttributeInfo,
    RecordAttributeType,
    RecordImportance,
    RecordModel
} from "../../../../src/models/RecordModel";

describe.skip("Pouch DB Workspace Tests", () => {
    const log = new KDAPLogger("Pouch DB Workspace Tests");
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
        e(ws).toBe(true)

        //Adding already exisiting workspace test
        try {
            await addWs();
            e(true).toBe(false); //Fail
        }
        catch (err) {
            //If it throws exception saying duplicate. Test passed
            e(true).toBe(true);
        }
    });

    test("Get Workspace Tests", async () => {
        //Simple get test after adding ws
        await addWs();
        const ws = await getWs();
        e(ws).toBeDefined();
        e(ws!.name).toBe(testWorkspaceName)
        e(ws!.desc).toBe(testWorkspaceDesc);

        //Simple get test on non existing ws
        const ws2 = await getWs("non-existing-ws");
        e(ws2).toBeUndefined();
    })

    test("Delete Workspace Tests", async () => {
        //Simple delete test
        await addWs();
        const result = await deleteWs();
        e(result).toBe(true);

        //Delete non existing ws
        const result2 = await deleteWs("non-existing-ws")
        e(result2).toBe(false);
    })

    test("Update Workspace Tests", async () => {
        const r = await addWs();
        e(r).toBe(true);
        const ws = new WorkspaceModel(testWorkspaceName, "UPDATED DESC");
        const result = await updateWs(ws);
        e(result).toBe(true);
        const updatedWs = await getWs();
        e(updateWs).toBeDefined();
        e(updatedWs!.desc).toBe("UPDATED DESC");

        //updating non existing ws
        const result2 = await updateWs(new WorkspaceModel("non-existing", 'desc'))
        e(result2).toBe(false);
    })

    test("Get Workspaces test", async () => {
        //Simpple zero gets
        let wss = await db.getWorkspaces(0, range);
        e(wss.length).toBe(0);

        //Simple single get
        await addWs();
        wss = await db.getWorkspaces(0, range);
        e(wss.length).toBe(1);
        await deleteWs();

        //Looped tests
        for (let i = 0; i < range; i++) {
            await addWs(i.toString());
        }

        wss = await db.getWorkspaces(0, range);
        e(wss.length).toBe(range);
    })
});


describe("Pouch Record table tests", () => {
    const log = new KDAPLogger("Pouch DB Workspace Tests");
    let db: PouchDb;
    const recName = "TEST_REC";
    const att: Map<string, IRecordAttributeInfo> = new Map();

    const addRec = async (n?: string, at?: Map<string, IRecordAttributeInfo>) => {
        let s = recName;
        let j = att;
        if (n) {
            s = n;
        }
        if (at) {
            j = at;
        }
        return await db.addRecord(s, j);
    }

    const delRec = async (name?: string) => {
        let s = recName;
        if (name) {
            s = name;
        }
        return await db.deleteRecord(s);
    }



    beforeEach(async () => {
        db = new PouchDb();
        await db.init();
        att.clear();
        att.set("id", { attributeImportance: RecordImportance.optional, attributeType: RecordAttributeType.float });
        att.set("att1", { attributeImportance: RecordImportance.optional, attributeType: RecordAttributeType.int });
        att.set("att2", { attributeImportance: RecordImportance.important, attributeType: RecordAttributeType.int});
    });

    afterEach(async () => {
        await db.deleteRecord(recName);

    });

    test("Create record test", async () => {
        //Simple add test
        const res = await addRec();
        e(res).toBe(true);
        const delRes = await delRec();
        e(delRes).toBe(true)
        //Adding without id
        const testAtt: Map<string, IRecordAttributeInfo> = new Map();
        testAtt.set("not id", { attributeImportance: RecordImportance.important, attributeType: RecordAttributeType.text });
        try {
            await addRec(recName, testAtt);
            e(true).toBe(false);
        }
        catch (err) {
            e(true).toBe(true);
        }
        //Attempt to add existing rec
        try {
            await addRec(recName, testAtt);
            e(true).toBe(false);
        }
        catch (err) {
            e(true).toBe(true);
        }
    })

    test("Delete record test", async () => {
        await addRec();
        let res = await delRec();
        e(res).toBe(true);
        res = await delRec();
        e(res).toBe(false);
    })

    test("Get record test", async () => {
        await addRec();
        const retreivedRec = await db.getRecord(recName) as RecordModel;
        e(retreivedRec).toBeDefined();
        e(retreivedRec.name).toBe(recName);
        e(retreivedRec.attributes.get("id")?.attributeType).toBe(RecordAttributeType.float)

        try {
            await db.getRecord("doesnt exist") as RecordModel;
            e(false).toBe(true)
        }
        catch (err) {
            e(true).toBe(true)
        }

    })

    //Remaining tests :-
    //Get records of workspace X, Get workspace using record X.



})