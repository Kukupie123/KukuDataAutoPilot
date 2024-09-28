package dev.kukukodes.kdap.dataBoxService.helper;

import dev.kukukodes.kdap.dataBoxService.exceptions.DefaultExceptionResponseCode;
import dev.kukukodes.kdap.dataBoxService.exceptions.ExceptionResponseCode;
import dev.kukukodes.kdap.dataBoxService.model.ResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHelper {

    public ResponseEntity<ResponseModel<Object>> kdapExceptionHandler(Exception ex) {
        logException(log, ex);
        if (ex instanceof ExceptionResponseCode exceptionResponseCode) {
            return ResponseModel.buildResponse(exceptionResponseCode.errorMessage(), null, exceptionResponseCode.getHttpStatus());
        }
        var defaultException = new DefaultExceptionResponseCode(ex);
        return ResponseModel.buildResponse(defaultException.errorMessage(), null, defaultException.getHttpStatus());
    }

    public void logException(Logger logger, Exception e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Message : ").append(e.getMessage());
        stringBuilder.append("\n");
        stringBuilder.append("--Stack trace--\n");
        for (var s : e.getStackTrace()) {
            stringBuilder.append(s);
            stringBuilder.append("\n");
        }
        logger.error(stringBuilder.toString());
    }
}
