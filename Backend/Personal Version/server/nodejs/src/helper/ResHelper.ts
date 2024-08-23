import { Response } from "express";
import { IResponseData } from "../models/response/IResponseData";
import { HttpStatusCode } from "../util/HttpCodes";

export function sendResponse(status: HttpStatusCode, msg: string, data: IResponseData, responseObject: Response) {
    const timestamp = new Date().toISOString();
    responseObject.status(status).json({
        msg: msg,
        data: data.getData(), // Directly use the data object
        timestamp: timestamp
    });
}

