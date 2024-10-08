import {RecordService} from "../services/record/RecordService";
import {IController} from "./IController";
import {ServiceManager} from "../services/ServiceManager";
import {KDAPLogger} from "../util/KDAPLogger";
import {IRecordAttributeInfo, RecordAttributeType, RecordImportance, RecordModel} from "../models/RecordModel";
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

    async addRecord(req: Request, res: Response) {
        const name = req.body.name;
        const desc = req.body.desc;
        const attributesString = req.body.attributes;
        const attributesJson = JSON.parse(attributesString); // Key is the attribute name, value is the "type;importance"

        const attributes = new Map<string, IRecordAttributeInfo>();

        for (const k in attributesJson) {
            const [type, importance] = (attributesJson[k] as string).split(";");
            this.logger.log({
                msg: `Type : ${type as RecordAttributeType}`
                , func: this.addRecord
            })

            switch (type) {
                case RecordAttributeType.int:
                case RecordAttributeType.text:
                case RecordAttributeType.float:
                case RecordAttributeType.date:
                    break;
                default:
                    return res.status(400).json({error: `Invalid type for ${k}:${type}`})
            }
            switch (importance) {
                case RecordImportance.optional:
                case RecordImportance.important:
                    break;
                default:
                    return res.status(500).json({error: `Invalid Importance ${k}:${importance}`})
            }

            attributes.set(k, {
                attributeType: type as RecordAttributeType,
                attributeImportance: importance as RecordImportance
            })
        }
        const result = await this.recService.addRecord(name, attributes, desc);
        sendResponse(result ? HttpStatusCode.OK : HttpStatusCode.EXPECTATION_FAILED,
            result ? "Added Record" : "Failed to add Record",
            new ResponseDataGeneric(null),
            res);

    }


    async getRecord(req: Request, res: Response) {
        const recID = req.params.id;
        this.logger.log({msg: `Getting record ${recID}`, func: this.getRecord});

        const rec = await this.recService.getRecord(recID);
        //Map is not directly supported by JSON.parse
        if (rec) {
            const attributesObj = Object.fromEntries(rec.attributes); // Convert Map to an Object
            const recWithObjAttributes = {
                ...rec,
                attributes: attributesObj
            };
            sendResponse(HttpStatusCode.OK, "Record retrieved", new ResponseDataGeneric(recWithObjAttributes), res);
        } else {
            sendResponse(HttpStatusCode.NOT_FOUND, "Not found", new ResponseDataGeneric<RecordModel | undefined>(undefined), res);
        }
    }

    async deleteRecord(req: Request, res: Response) {
        const recID = req.params.id;
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
        const recs =  await this.recService.getRecords(skip, limit);
        const resRecs = recs.map(rec=>{
            const attrObj = Object.fromEntries(rec.attributes);
            return {
                ...rec,
                attributes: attrObj
            };
        })
        sendResponse(200,"Retrieved",new ResponseDataGeneric(resRecs),res);
    }
}