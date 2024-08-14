import { DatabaseConfig } from "./config/DatabaseConfig";
import { DatabaseFactory } from "./Factory/DatabaseFactory";
import { IDatabaseAdapter } from "./adapters/IDatabaseAdapter";

export class DatabaseService {

    private static instance: DatabaseService; // For singleton
    private adapter!: IDatabaseAdapter;

    // Private constructor to make sure that object can't be created of this class from the outside.
    private constructor() {
    }

    public static async getInstance(config?: DatabaseConfig): Promise<DatabaseService> {
        if (!DatabaseService.instance) {
            DatabaseService.instance = new DatabaseService();
            //Using factory to get the correct adapter based on config.
            //If instance is already valid then config has no use
            if (config)
                this.instance.adapter = await DatabaseFactory.BUILD(config);
            else throw new Error("Config is mandatory when initializing for the first time");
        }
        return DatabaseService.instance;
    }
}
