import { IDatabaseAdapter } from "./Adapter/IDatabaseAdapter";
import { DatabaseConfig } from "./DatabaseConfig";
import { DatabaseFactory } from "./DatabaseFactory";

export class DatabaseService {
    private static instance: DatabaseService;
    private adapter: IDatabaseAdapter;

    private constructor(config: DatabaseConfig) {
        this.adapter = DatabaseFactory.createAdapter(config);
    }

    public static getInstance(config: DatabaseConfig): DatabaseService {
        if (!DatabaseService.instance) {
            DatabaseService.instance = new DatabaseService(config);
        }
        return DatabaseService.instance;
    }

}