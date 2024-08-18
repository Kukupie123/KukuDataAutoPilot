import { createDirectory } from "../../../helper/DirHelper";
import { IRecordAttribute } from "../../../models/RecordModel";
import { ResponseException } from "../../../models/exception/ResponseException";
import { HttpStatusCode } from "../../../util/HttpCodes";

//Pouch Db config
export const rootDir = "data/db/pouch";
export const workspaceDbDir = `${rootDir}/workspace`;
export const recordDbDir = `${rootDir}/record`;
export const userTableDir = `${rootDir}/user-table`;

/**
 * Checks if attribute is valid. Throws exception if not valid
 * @param attributes 
 */
export function validateAttribute(attributes: IRecordAttribute[]): void {
    attributes.forEach((att) => {

        // Ensure that each attribute object has an '_id' field
        if (!att['_id']) {
            throw new ResponseException("Attribute is missing '_id' field", HttpStatusCode.BAD_REQUEST);
        }

        // Validate each attribute's value
        for (const [key, value] of Object.entries(att)) {
            const dataType = value.type;
            const importance = value.importance;

            // Validate importance
            if (!importance || !(importance === "important" || importance === "optional")) {
                throw new ResponseException("Importance is missing or invalid. It needs to be 'important' or 'optional'", HttpStatusCode.BAD_REQUEST);
            }

            // Validate dataType
            switch (dataType) {
                case "int":
                case "float":
                case "text":
                case "date":
                    break;
                default:
                    throw new ResponseException(`Invalid data type for attribute ${key}. It needs to be 'int', 'float', 'text', or 'date'`, HttpStatusCode.BAD_REQUEST);
            }
        }
    });
}



export function getUserTableDirectory(workspaceID: string, recordID: string): string {
    const dir = `${userTableDir}/${workspaceID}/${recordID}`;
    return dir;
}
