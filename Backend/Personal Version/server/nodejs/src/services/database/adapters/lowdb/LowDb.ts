import { WorkspaceModel } from "../../../../models/WorkspaceModel";
import { IDatabaseAdapter } from "../IDatabaseAdapter";
import { sleep } from "../../../../util/Sleep"// Adjust the path as needed

export class LowDb implements IDatabaseAdapter {

    async init(): Promise<void> {
        await sleep(2000); // Sleep for 2000 milliseconds (2 seconds)
        console.log("Fake init done");
    }

    async dispose(): Promise<void> {
        // Implement dispose logic if needed
        console.log("Dispose called");
    }

    async addWorkspace(workspace: WorkspaceModel): Promise<boolean> {
        // Implement addWorkspace logic
        console.log("Add workspace called");
        return true;
    }

    async updateWorkspace(id: string, updatedWorkspace: WorkspaceModel): Promise<boolean> {
        // Implement updateWorkspace logic
        console.log("Update workspace called");
        return true;
    }
}
