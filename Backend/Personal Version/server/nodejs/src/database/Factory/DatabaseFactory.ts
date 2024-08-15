import { IDatabaseAdapter } from "../IDatabaseAdapter";
import { BuildOption } from "./BuildOptions";
import { source as dbSource, DatabaseType } from "../../config/database.config";
import { PouchDb } from "../adapters/PouchDb";
import { Logger as WintonLogger } from "winston";
import { Logger as UtilLogger } from "../../util/Logger";
//TODO: Make use of GraphQL
export class DatabaseFactory {
    private static Logger: WintonLogger = UtilLogger.CreateLogger(DatabaseFactory.name);
    public static async Build(option?: BuildOption): Promise<IDatabaseAdapter> {
        DatabaseFactory.Logger.info(`Building Database Adapter with option ${option}`)
        switch (dbSource) {
            case DatabaseType.PouchDb:
                DatabaseFactory.Logger.info(`Building PouchDb Adapter`)
                const db = new PouchDb();
                DatabaseFactory.Logger.info(`Initializing PouchDb Adapter`)
                await db.init();
                DatabaseFactory.Logger.info(`Initialized PouchDb Adapter`)
                return db;
            default:
                throw new Error("Invalid dbType in database config");
        }
    }
}