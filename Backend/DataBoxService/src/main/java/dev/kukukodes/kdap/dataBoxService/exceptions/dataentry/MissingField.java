package dev.kukukodes.kdap.dataBoxService.exceptions.dataentry;

import dev.kukukodes.kdap.dataBoxService.enums.DataBoxFieldType;
import dev.kukukodes.kdap.dataBoxService.exceptions.ExceptionResponseCode;
import org.springframework.http.HttpStatus;

public class MissingField extends Exception implements ExceptionResponseCode {
    private final String fieldName;
    private final DataBoxFieldType fieldType;

    public MissingField(String fieldName, DataBoxFieldType fieldType) {
        super(
                String.format("Missing field '%s' of type '%s'", fieldName, fieldType)
        );

        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    @Override
    public String errorMessage() {
        return String.format("Missing field '%s' of type '%s'", fieldName, fieldType);

    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
