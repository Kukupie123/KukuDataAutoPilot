package dev.kukukodes.kdap.dataBoxService.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.FileNotFoundException;

public class DefaultExceptionResponseCode implements ExceptionResponseCode {
    private final HttpStatus status;
    private final String message;
    private final Exception exception;

    public DefaultExceptionResponseCode(Exception exception) {
        this.exception = exception;
        if (exception instanceof AccessDeniedException) {
            this.status = HttpStatus.FORBIDDEN;
            this.message = "Access denied";
            return;
        } else if (exception instanceof BadCredentialsException) {
            this.status = HttpStatus.UNAUTHORIZED;
            this.message = "Bad credentials";
            return;
        } else if (exception instanceof FileNotFoundException) {
            this.status = HttpStatus.NOT_FOUND;
            this.message = "File not found";
            return;
        }
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = "Something went wrong";
    }

    @Override
    public String errorMessage() {
        return message + " : " + exception.getMessage();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }
}
