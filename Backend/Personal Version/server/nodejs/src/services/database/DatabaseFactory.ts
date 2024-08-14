import { LowDb } from "./adapters/lowdb/LowDb";
import { DatabaseConfig } from "./DatabaseConfig";

//Responsible for creating object of the correct adapter based on config
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