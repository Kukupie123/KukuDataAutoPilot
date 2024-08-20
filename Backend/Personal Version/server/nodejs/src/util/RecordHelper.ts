import { IRecordAttributeInfo } from "../models/RecordModel";

/**
 * Checks if attribute is valid. Throws an exception if not valid.
 * @param attributes An array of IRecordAttribute objects to validate.
 */
export function validateAttribute(attributes: Map<string, IRecordAttributeInfo>): void {
    if (!attributes.has("id")) {
        throw new Error("id attribute is missing");
    }

}
