package dev.kukukodes.kdap.dataBoxService.exceptions.dataentry;

import dev.kukukodes.kdap.dataBoxService.enums.DataBoxFieldType;
import dev.kukukodes.kdap.dataBoxService.exceptions.ExceptionResponseCode;
import org.springframework.http.HttpStatus;

public class InvalidFieldValue extends Exception implements ExceptionResponseCode {
    private final String fieldName;
    private final DataBoxFieldType fieldType;
    private final String fieldValue;

    public InvalidFieldValue(String fieldName, String fieldValue, DataBoxFieldType fieldType) {
        super(
                String.format("Invalid value (%s) for field (%s). Expected type is (%s)", fieldValue, fieldName, fieldType)
        );
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldValue = fieldValue;
    }

    public InvalidFieldValue(DataBoxFieldType fieldType, String fieldValue) {
        this(null, fieldValue, fieldType);
    }

    @Override
    public String errorMessage() {
        return String.format("Invalid value (%s) for field (%s). Expected type is (%s)", fieldValue, fieldName, fieldType);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
