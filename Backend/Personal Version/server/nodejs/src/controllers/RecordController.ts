import {RecordService} from "../services/record/RecordService";
import {IController} from "./IController";
import {ServiceManager} from "../services/ServiceManager";
import {KDAPLogger} from "../util/KDAPLogger";
import {IRecordAttributeInfo, RecordModel} from "../models/RecordModel";
import {Request, Response} from "express"
import {sendResponse} from "../helper/ResHelper";
import {HttpStatusCode} from "../util/HttpCodes";
import {ResponseDataGeneric} from "../models/response/ResponseDataGeneric";

export class RecordController implements IController {
    private logger = new KDAPLogger(RecordController.name);
    private recService!: RecordService;

    async initController(): Promise<void> {
        await ServiceManager.Register(RecordService);
        this.recService = ServiceManager.GetService(RecordService)
    }

    /**
     * Expected payload {name:"",attributes:"{type;importance}",desc:""}
     * @returns
     */
    async addRecord(req: Request, res: Response) {
        const name = req.body['name'];
        const attributesString = req.body['attributes'];
        const attributeJSON = JSON.parse(attributesString) as [string, IRecordAttributeInfo][];
        const attributes = new Map(attributeJSON)
        const desc = req.body['desc'];
        this.logger.log({
            msg: `Adding Record ${name}, with attributes ${JSON.stringify(Array.from(attributes))}`,
            func: this.addRecord
        })
        const success = await this.recService.addRecord(name, attributes, desc);
        let statusCode = 200;
        let msg = 'Added';
        if (!success) {
            statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
            msg = 'Something went wrong';
        }
        this.logger.log({msg: `add record status : ${success}`, func: this.addRecord})
        sendResponse(statusCode, msg, new ResponseDataGeneric<boolean>(success), res);
    }

    async getRecord(req: Request, res: Response) {
        const recID = req.params['id']
        this.logger.log({msg: `Getting record ${recID}`, func: this.getRecord});
        const rec = await this.recService.getRecord(recID);
        let statusCode = 200;
        let msg = "Added";
        if (!rec) {
            statusCode = HttpStatusCode.NOT_FOUND;
            msg = "Not found";
        }
        sendResponse(statusCode, msg, new ResponseDataGeneric<RecordModel | undefined>(rec), res)
    }

    async deleteRecord(req: Request, res: Response) {
        const recID = req.params['id'];
        this.logger.log({msg: `Deleting record ${recID}`, func: this.deleteRecord});
        const result = await this.recService.deleteRecord(recID);
        let statusCode = 200;
        let msg = "Deleted";
        if (!result) {
            statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
            msg = "Failed to delete";
        }
        sendResponse(statusCode, msg, new ResponseDataGeneric<boolean>(result), res);
    }

    async getRecords(req: Request, res: Response) {
        const skip = parseInt(req.query['skip'] as string, 0) || 0;
        const limit = parseInt(req.query['limit'] as string, 10) || 10;
        this.logger.log({msg: `Getting ALL records with skip ${skip}, limit : ${limit} `, func: this.getRecord});
        return await this.recService.getRecords(skip, limit);
    }
}