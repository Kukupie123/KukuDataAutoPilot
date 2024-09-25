package dev.kukukodes.kdap.dataBoxService.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel<T> {
    private String message;
    private T data;

    private ResponseModel(String msg, T data) {
        this.message = msg;
        this.data = data;
    }

    public static <T> ResponseEntity<ResponseModel<T>> success(String msg, T data) {
        return ResponseEntity.ok(new ResponseModel<>(msg, data));
    }

    public static <T> ResponseEntity<ResponseModel<T>> buildResponse(String msg, T data, int statusCode) {
        return ResponseEntity.status(statusCode).body(new ResponseModel<>(msg, data));
    }
}
