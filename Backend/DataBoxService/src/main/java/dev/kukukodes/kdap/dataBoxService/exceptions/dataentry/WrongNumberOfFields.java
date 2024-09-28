package dev.kukukodes.kdap.dataBoxService.exceptions.dataentry;

import dev.kukukodes.kdap.dataBoxService.exceptions.ExceptionResponseCode;
import org.springframework.http.HttpStatus;

public class WrongNumberOfFields extends Exception implements ExceptionResponseCode {
    private final int requiredSize;
    private final int actualSize;

    public WrongNumberOfFields(int requiredSize, int actualSize) {
        super(String.format("Required fields %s but got %S", requiredSize, actualSize));
        this.requiredSize = requiredSize;
        this.actualSize = actualSize;
    }

    @Override
    public String errorMessage() {
        return String.format("Required fields %s but got %S", requiredSize, actualSize);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
