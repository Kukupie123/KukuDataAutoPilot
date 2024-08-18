import { Response, Request } from "express";
import { ServiceFactory } from "../services/ServiceFactory";
import { RecordService } from "../services/record/RecordService";
import { IController } from "./IController";
import { RecordModel } from "../models/RecordModel";

export class RecordController implements IController {
    private recService!: RecordService;
    async initController(): Promise<void> {
        this.recService = await ServiceFactory.Build(RecordService);
    }

    async createRecord(req: Request, res: Response) {
    }

    async getRecords(req: Request, res: Response) {
    }

    /**
     *  Payload Example :-
     *  {
     *      "name": "rec3",
     *      "workspaceID": "ws1",
     *      "attributes": "{ \"_id\":\"text;mandatory\",  \"name\": \"text;optional\"}"
     *  }
     * "_id" is mandatory. "text;mandatory/optional" is also mandatory.
     */
    async addEntry(req: Request, res: Response) {

    }
}