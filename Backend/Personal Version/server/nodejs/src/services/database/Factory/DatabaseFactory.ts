import { IDatabaseAdapter } from "../adapters/IDatabaseAdapter";
import { LowDb } from "../adapters/lowdb/LowDb";
import { DatabaseConfig } from "../config/DatabaseConfig";

//Builds the correct database adapter
export class DatabaseFactory {
    public static async BUILD(config: DatabaseConfig): Promise<IDatabaseAdapter> {
        switch (config.dbType) {
            case "lowdb":
                const db = new LowDb();
                await db.init();
                return db;
            default:
                throw new Error("Invalid dbType");
        }
    }
}