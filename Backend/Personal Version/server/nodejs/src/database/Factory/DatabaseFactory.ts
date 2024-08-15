import { IDatabaseAdapter } from "../IDatabaseAdapter";
import { BuildOption } from "./BuildOptions";
import { source as dbSource, DatabaseType } from "../../config/database.config";
import { PouchDb } from "../adapters/PouchDb";
import { KDAPLogger } from "../../util/EnhancedLogger";
import { Category } from "../../config/kdapLogger.config";
//TODO: Make use of GraphQL
export class DatabaseFactory {
    private static Logger = new KDAPLogger(DatabaseFactory.name);
    public static async Build(option?: BuildOption): Promise<IDatabaseAdapter> {
        DatabaseFactory.Logger.log(Category.Factory, `Building Database Adapter with option ${option}`)
        switch (dbSource) {
            case DatabaseType.PouchDb:
                DatabaseFactory.Logger.log(Category.Factory, `Building PouchDb Adapter`)
                const db = new PouchDb();
                DatabaseFactory.Logger.log(Category.Factory, `Initializing PouchDb Adapter`)
                await db.init();
                DatabaseFactory.Logger.log(Category.Factory, `Initialized PouchDb Adapter`)
                return db;
            default:
                throw new Error("Invalid dbType in database config");
        }
    }
}