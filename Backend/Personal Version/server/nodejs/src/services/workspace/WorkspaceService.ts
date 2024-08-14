import { DatabaseFactory } from "../../database/Factory/DatabaseFactory";
import { IDatabaseAdapter } from "../../database/IDatabaseAdapter";
import { sleep } from "../../util/Sleep";

export class WorkspaceService {
    private db!: IDatabaseAdapter;
    public async init(): Promise<void> {
        console.log("workspace service fake init started");
        this.db = await DatabaseFactory.Build();
        await sleep(2000);
        console.log("workspace service fake init done");
    }
}