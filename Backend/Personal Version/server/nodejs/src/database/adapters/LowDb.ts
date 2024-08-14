import { log } from "console";
import { IDatabaseAdapter } from "../IDatabaseAdapter";
import { sleep } from "../../util/Sleep";

export class LowDb implements IDatabaseAdapter {
    async init(): Promise<void> {
        log("Fake db init started");
        await sleep(2000);
        log("Fake db init done");
    }

    async dispose(): Promise<void> {
        log("Fake dispose started");
        await sleep(500);
        log("fake dispose done");
    }
}