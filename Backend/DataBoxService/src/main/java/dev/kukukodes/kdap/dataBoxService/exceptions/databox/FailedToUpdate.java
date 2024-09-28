package dev.kukukodes.kdap.dataBoxService.exceptions.databox;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.exceptions.ExceptionResponseCode;
import org.springframework.http.HttpStatus;

public class FailedToUpdate extends RuntimeException implements ExceptionResponseCode {
    private final DataBox dataEntry;

    public FailedToUpdate(DataBox dataEntry) {
        super(
                String.format("Failed to update data box %s", dataEntry)
        );
        this.dataEntry = dataEntry;
    }

    @Override
    public String errorMessage() {
        return String.format("Failed to update data box %s", dataEntry);

    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.EXPECTATION_FAILED;
    }
}
