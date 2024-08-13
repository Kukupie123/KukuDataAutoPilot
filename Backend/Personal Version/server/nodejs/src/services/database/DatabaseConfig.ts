export class DatabaseConfig {
    dbType: string = 'lowdb';

    constructor(dbType?: string) {
        if (dbType) {
            this.dbType = dbType;
        }
    }
}
