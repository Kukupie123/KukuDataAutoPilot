package dev.kukukodes.kdap.dataBoxService.helper;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBoxFieldDescriptor;
import dev.kukukodes.kdap.dataBoxService.entity.dataEntry.DataEntry;
import dev.kukukodes.kdap.dataBoxService.enums.DataBoxFieldType;
import dev.kukukodes.kdap.dataBoxService.exceptions.dataentry.InvalidFieldValue;
import dev.kukukodes.kdap.dataBoxService.exceptions.dataentry.MissingField;
import dev.kukukodes.kdap.dataBoxService.exceptions.dataentry.WrongNumberOfFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DataEntryHelper {
    /**
     * Checks if all required fields are present and valid for the data box's fields
     *
     * @return true if it's valid.
     */
    public boolean validateEntryForDataBox(Map<String, DataBoxFieldDescriptor> boxFields, DataEntry dataEntry) throws MissingField, InvalidFieldValue, WrongNumberOfFields {
        //Validate entries and fields size
        if (dataEntry.getValues().size() > boxFields.size()) {
            throw new WrongNumberOfFields(boxFields.size(),dataEntry.getValues().size());
        }

        Map<String, String> entries = new HashMap<>(dataEntry.getValues());
        //Check each entry
        for (Map.Entry<String, DataBoxFieldDescriptor> field : boxFields.entrySet()) {
            String fieldName = field.getKey();
            DataBoxFieldDescriptor fieldDescription = field.getValue();
            String entryValue = entries.get(fieldName);
            //If entry is null check if it's required or optional
            if (entryValue == null) {
                if (fieldDescription.isRequired()) {
                    throw new MissingField(fieldName, field.getValue().getFieldType());
                }
                continue;
            }
            DataBoxFieldType requiredDataType = fieldDescription.getFieldType();
            try {
                if (!isEntryValueValid(requiredDataType, entryValue)) {
                    throw new InvalidFieldValue(fieldName, entryValue, field.getValue().getFieldType());
                }
            } catch (NumberFormatException e) {
                throw new InvalidFieldValue(fieldName, entryValue, field.getValue().getFieldType());
            }

        }
        return true;
    }

    /**
     * Checks if the string value is valid and can be stored as a valid value for the specified field type
     *
     * @param fieldType  the field type to check against
     * @param entryValue the value to check
     * @return true if it's valid entry value for the field type
     */
    public boolean isEntryValueValid(DataBoxFieldType fieldType, String entryValue) {
        switch (fieldType) {
            case DATE:
                LocalDate parsed = LocalDate.parse(entryValue);
                return true;
            case INTEGER_NUMBER:
                int parsedNum = Integer.parseInt(entryValue);
                return true;
            case REAL_NUMBER:
                float parsedFloat = Float.parseFloat(entryValue);
                return true;
            case TEXT:
                return true;
            case TRUE_FALSE:
                //It has to be either true or false as text
                if (entryValue.equalsIgnoreCase("true") || entryValue.equalsIgnoreCase("false")) {
                    return true;
                } else {
                    log.error("invalid true_false value for entry {}", entryValue);
                    return false;
                }
            default:
                throw new RuntimeException(new Exception("required data type did not match"));
        }
    }

}
