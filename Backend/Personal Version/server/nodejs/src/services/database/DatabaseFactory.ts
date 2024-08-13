import { LowDb } from "./Adapter/LowDb/LowDb";
import { DatabaseConfig } from "./DatabaseConfig";

export class DatabaseFactory {
    public static createAdapter(config: DatabaseConfig) {
        switch (config.dbType) {
            case "lowdb":
                return new LowDb();
                break;
            default:
                throw new Error("Unknown Database Type")
        }
    }
}