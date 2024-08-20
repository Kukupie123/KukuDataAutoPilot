import { beforeEach, afterEach, describe, expect, test, beforeAll } from "@jest/globals";
import { PouchDb } from "../../../src/database/adapters/pouchDB/PouchDb";
import { IDatabaseAdapter } from "../../../src/database/interface/IDatabaseAdapter";
import { ServiceManager } from "../../../src/services/ServiceManager";
import { WorkspaceService } from "../../../src/services/workspace/WorkspaceService";
import { WorkspaceModel } from "../../../src/models/WorkspaceModel";

describe("Workspace service tests", () => {
    const db: IDatabaseAdapter = new PouchDb;
    let service: WorkspaceService;

    const wsName = "TEST_WS";

    const addWs = async (name?: string) => {
        let n = wsName
        if (name) {
            n = name;
        }
        return await service.addWorkspace(n);
    }
    const getWs = async (name?: string) => {
        let n = wsName
        if (name) {
            n = name;
        }
        return await service.getWorkspace(n);
    }

    const updateWs = async (ws: WorkspaceModel) => {
        return await service.updateWorkspace(ws);
    }

    const deleteWs = async (name?: string) => {
        let n = wsName;
        if (name) {
            n = name
        }
        return await service.deleteWorkspace(n);
    }

    beforeAll(async () => {
        await ServiceManager.Register(WorkspaceService);
        service = ServiceManager.GetService(WorkspaceService);

    })

    beforeEach(async () => {
        await db.init();
    })

    afterEach(async () => {
        await service.deleteWorkspace(wsName)

    })

    test("Add Workspace Test", async () => {
        console.log("Add workspace test")
        //Simple add test
        const ws = await addWs();
        expect(ws).toBe(true);

        //Add existing test
        try {
            await addWs();
            expect(true).toBe(false);
        }
        catch (err) {
            expect(true).toBe(true);
        }
    })

    test("Get workspace test", async () => {
        console.log("Get Workspace Test")
        //Simple get test
        const addResult = await addWs();
        expect(addResult).toBe(true);
        const ws = await getWs();
        expect(ws).toBeDefined();
        expect(ws!.name).toBe(wsName)

        //Getting non existing ws
        expect((await getWs("Doesnt exist"))).toBeUndefined();
    })

    test("Update workspace test", async () => {
        console.log("Update workspace test")
        //Simple update test
        const addResult = await addWs();
        expect(addResult).toBe(true);
        let ws = await getWs();
        expect(ws).toBeDefined();
        expect(ws!.name).toBe(wsName);
        ws!.desc = "NEW DESC";
        const updateResult = await updateWs(ws!);
        expect(updateResult).toBe(true);

        //Updating non existing ws
        expect(await updateWs(new WorkspaceModel("Doesnt exist", "SOME"))).toBe(false);
    })

    test("Delete ws", async () => {
        await addWs();
        let r = await deleteWs();
        expect(r).toBe(true);

        //Deleting nonexisting ws
        const r2 = await deleteWs("non existing");
        console.log("R ISSSSSSS " + r);
        expect(r2).toBe(false);
    })

    test("Get workspaces test", async () => {
        const e = await service.getWorkspaces(0, 100);
        expect(e.length).toBe(0)
        //Simple get test
        await addWs();
        const ws = await service.getWorkspaces(0, 100);
        expect(ws[0]).toBeDefined();
        expect(ws[0].name).toBe(wsName);

    })
})