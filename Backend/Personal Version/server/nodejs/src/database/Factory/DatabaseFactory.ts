import { IDatabaseAdapter } from "../IDatabaseAdapter";
import { BuildOption } from "./BuildOptions";
import { source as dbSource, DatabaseType } from "../../config/database.config";
import { PouchDb } from "../adapters/PouchDb";
import { KDAPLogger } from "../../util/KDAPLogger";
import { Category } from "../../config/kdapLogger.config";
//TODO: Make use of GraphQL
export class DatabaseFactory {
    private static Logger = new KDAPLogger(DatabaseFactory.name, Category.Factory);
    public static async Build(option?: BuildOption): Promise<IDatabaseAdapter> {
        DatabaseFactory.Logger.log(`Building Database Adapter with option ${option}`)
        switch (dbSource) {
            case DatabaseType.PouchDb:
                DatabaseFactory.Logger.log(`Building PouchDb Adapter`)
                const db = new PouchDb();
                DatabaseFactory.Logger.log(`Initializing PouchDb Adapter`)
                await db.init();
                DatabaseFactory.Logger.log(`Initialized PouchDb Adapter`)
                return db;
            default:
                throw new Error("Invalid dbType in database config");
        }
    }
}