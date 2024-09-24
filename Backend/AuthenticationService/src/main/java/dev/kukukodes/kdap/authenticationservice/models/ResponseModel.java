package dev.kukukodes.kdap.authenticationservice.models;

import lombok.*;
import org.springframework.http.ResponseEntity;

@Data
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
