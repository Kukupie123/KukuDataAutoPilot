package dev.kukukodes.kdap.dataBoxService.helper;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class LogHelper {

    public void logException(Logger logger, Exception e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Message : " + e.getMessage());
        stringBuilder.append("\n");
        stringBuilder.append("--Stack trace--\n");
        for (var s : e.getStackTrace()) {
            stringBuilder.append(s);
            stringBuilder.append("\n");
        }
        logger.error(stringBuilder.toString());
    }
}
