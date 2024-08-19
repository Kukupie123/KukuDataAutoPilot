import { IDatabaseAdapter } from "../IDatabaseAdapter";
import { BuildOption } from "./BuildOptions";
import { source as dbSource, DatabaseType } from "../../config/database.config";
import { PouchDb } from "../adapters/pouchDB/PouchDb";
import { KDAPLogger } from "../../util/KDAPLogger";
//TODO: Make use of GraphQL
export class DatabaseFactory {
    private static Logger = new KDAPLogger(DatabaseFactory.name);
    public static async Build(option?: BuildOption): Promise<IDatabaseAdapter> {
        DatabaseFactory.Logger.log({ msg: `Building Database Adapter`, func: this.Build })
        switch (dbSource) {
            case DatabaseType.PouchDb:
                DatabaseFactory.Logger.log({ msg: `Building Pouch Adapter`, func: this.Build })
                const db = new PouchDb();
                DatabaseFactory.Logger.log({ msg: `Initializing Pouch Adapter`, func: this.Build })
                await db.init();
                DatabaseFactory.Logger.log({ msg: `Initialized Pouch Adapter`, func: this.Build })
                return db;
            default:
                throw new Error("Invalid dbType in database config");
        }
    }
}