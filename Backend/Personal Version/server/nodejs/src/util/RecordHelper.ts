import { IRecordAttributeInfo } from "../models/RecordModel";

/**
 * Checks if attribute is valid. Throws an exception if not valid.
 * @param attributes An array of IRecordAttribute objects to validate.
 */
export function validateAttribute(attributes: Map<string, IRecordAttributeInfo>): void {
    if (!attributes.has("id")) {
        throw new Error("id attribute is missing");
    }

    attributes.forEach((v, k) => {
        switch (v.attributeImportance) {
            case "important":
            case "optional":
                break;
            default:
                throw new Error(`Invalid attributeImportance ${v.attributeImportance}`);
        }

        switch (v.attributeType) {
            case "date":
            case "float":
            case "int":
            case "text":
                break;
            default:
                throw new Error(`Invalid attributeType ${v.attributeType}`);
        }
    })

}
