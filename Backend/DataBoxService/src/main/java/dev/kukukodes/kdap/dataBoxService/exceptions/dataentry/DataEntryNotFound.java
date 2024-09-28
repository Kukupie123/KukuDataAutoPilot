package dev.kukukodes.kdap.dataBoxService.exceptions.dataentry;

import dev.kukukodes.kdap.dataBoxService.exceptions.ExceptionResponseCode;
import org.springframework.http.HttpStatus;

public class DataEntryNotFound extends Exception implements ExceptionResponseCode {
    public DataEntryNotFound(String id) {
        super("Failed to find data entry with id " + id);
    }

    @Override
    public String errorMessage() {
        return getMessage();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
