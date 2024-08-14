export class WorkspaceService {
    private static instance: WorkspaceService;
    private constructor() {

    }

    public static async getInstance(): Promise<WorkspaceService> {
        if (!this.instance) {
            this.instance = new WorkspaceService();
        }
        return this.instance;
    }
}