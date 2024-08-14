import { IDatabaseAdapter } from "../IDatabaseAdapter";
import { BuildOption } from "./BuildOptions";
import { source as dbSource, DatabaseType } from "../../config/database.config";
import { PouchDb } from "../adapters/PouchDb";
//TODO: Make use of GraphQL
export class DatabaseFactory {

    public static async Build(option?: BuildOption): Promise<IDatabaseAdapter> {
        switch (dbSource) {
            case DatabaseType.LowDb:
                const db = new PouchDb();
                await db.init();
                return db;
            default:
                throw new Error("Invalid dbType in database config");
        }
    }
}