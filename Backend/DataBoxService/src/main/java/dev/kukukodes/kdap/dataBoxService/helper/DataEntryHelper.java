package dev.kukukodes.kdap.dataBoxService.helper;

import dev.kukukodes.kdap.dataBoxService.entity.dataEntry.DataEntry;
import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBoxField;
import dev.kukukodes.kdap.dataBoxService.enums.DataBoxFieldType;
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
    public boolean validateEntryForDataBox(DataBox dataBox, DataEntry dataEntry) {

        if (dataEntry.getValues().size() > dataBox.getFields().size()) {
            log.error("required fields count : {} but got entries : {}", dataBox.getFields().size(), dataEntry.getValues().size());
            return false;
        }
        //Create map of fast access

        Map<String, DataBoxField> fieldsMap = new HashMap<>();

        dataBox.getFields().forEach(dataBoxField -> fieldsMap.put(dataBoxField.getFieldName(), dataBoxField));
        Map<String, String> entryValuesMap = new HashMap<>(dataEntry.getValues());

        //Loop fieldsMap
        for (Map.Entry<String, DataBoxField> entry : fieldsMap.entrySet()) {
            String fieldName = entry.getKey();
            DataBoxField dataBoxField = entry.getValue();
            String entryValue = entryValuesMap.get(fieldName);
            if (entryValue == null) {
                if (dataBoxField.isRequired()) {
                    log.error("Required field {} is missing from entry", fieldName);
                    return false;
                }
                continue;
            }
            DataBoxFieldType requiredDataType = dataBoxField.getFieldType();
            if (!isEntryValueValid(requiredDataType, entryValue)) {
                log.error("Required field {} is invalid from entry {}", fieldName, entryValue);
                return false;
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
