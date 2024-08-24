import {KDAPLogger} from "../../../util/KDAPLogger";
import {ICusomtIndexAdapter} from "../../interface/ICusomtIndexAdapter";

export class PouchDbIndexTable implements ICusomtIndexAdapter {
    private wsRecIndex: PouchDB.Database;
    private recWsIndex: PouchDB.Database;
    private logger: KDAPLogger;

    constructor(wsRecIndex: PouchDB.Database, recWsIndex: PouchDB.Database) {
        this.wsRecIndex = wsRecIndex;
        this.recWsIndex = recWsIndex;
        this.logger = new KDAPLogger(PouchDbIndexTable.name);
    }

    async link(recordIDWorkspaceIDTuples: [string: string][]): Promise<string[]> {
        this.logger.log({
            msg: `Linking record&workspace : ${JSON.stringify(recordIDWorkspaceIDTuples)}`,
            func: this.link
        })
        return Promise.resolve([]);
    }
}
