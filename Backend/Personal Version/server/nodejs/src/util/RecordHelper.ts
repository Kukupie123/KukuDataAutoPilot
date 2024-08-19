import { IRecordAttribute } from "../models/RecordModel";

/**
 * Checks if attribute is valid. Throws exception if not valid
 * @param attributes 
 */
export function validateAttribute(attributes: IRecordAttribute[]): void {
    attributes.forEach((att) => {

        // Ensure that each attribute object has an '_id' field
        if (!att['_id']) {
            throw new Error("Attribute is missing '_id' field");
        }

        // Validate each attribute's value
        for (const [key, value] of Object.entries(att)) {
            const dataType = value.type;
            const importance = value.importance;

            // Validate importance
            if (!importance || !(importance === "important" || importance === "optional")) {
                throw new Error("Importance is missing or invalid. It needs to be 'important' or 'optional'");
            }

            // Validate dataType
            switch (dataType) {
                case "int":
                case "float":
                case "text":
                case "date":
                    break;
                default:
                    throw new Error(`Invalid data type for attribute ${key}. It needs to be 'int', 'float', 'text', or 'date'`);
            }
        }
    });
}