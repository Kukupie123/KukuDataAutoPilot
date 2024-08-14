import { DatabaseConfig } from "./config/DatabaseConfig";
import { DatabaseFactory } from "./Factory/DatabaseFactory";
import { IDatabaseAdapter } from "./adapters/IDatabaseAdapter";

export class DatabaseService {

    private static instance: DatabaseService; // For singleton
    private adapter!: IDatabaseAdapter;

    // Private constructor to ensure that the DatabaseService instance cannot be created from outside the class.
    private constructor() {
    }

    // Static method to get the singleton instance of DatabaseService.
    // It initializes the instance with the provided configuration if it is not already initialized.
    // Throws an error if a configuration is required but not provided during the initial setup.
    public static async getInstance(config?: DatabaseConfig): Promise<DatabaseService> {
        if (!DatabaseService.instance) {
            DatabaseService.instance = new DatabaseService();
            // Use the factory to create the appropriate adapter based on the provided configuration.
            // The configuration is mandatory only for the first-time initialization.
            if (config)
                this.instance.adapter = await DatabaseFactory.BUILD(config);
            else
                throw new Error("Config is mandatory when initializing for the first time");
        }
        return DatabaseService.instance;
    }

}
