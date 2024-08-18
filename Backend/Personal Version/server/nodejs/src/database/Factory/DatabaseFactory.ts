import { IDatabaseAdapter } from "../IDatabaseAdapter";
import { BuildOption } from "./BuildOptions";
import { source as dbSource, DatabaseType } from "../../config/database.config";
import { PouchDb } from "../adapters/pouchDB/PouchDb";
import { KDAPLogger } from "../../util/KDAPLogger";
//TODO: Make use of GraphQL
export class DatabaseFactory {
    private static Logger = new KDAPLogger(DatabaseFactory.name);
    public static async Build(option?: BuildOption): Promise<IDatabaseAdapter> {
        DatabaseFactory.Logger.log(`Building Database Adapter`)
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