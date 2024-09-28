package dev.kukukodes.kdap.dataBoxService.exceptions;

import org.springframework.http.HttpStatus;

public interface ExceptionResponseCode {
    String errorMessage();
    HttpStatus getHttpStatus();
}
