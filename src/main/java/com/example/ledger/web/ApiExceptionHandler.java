package com.example.ledger.web;

import com.example.ledger.service.TransferService.AccountNotFoundException;
import com.example.ledger.service.TransferService.TransferRejectedException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> invalid(MethodArgumentNotValidException e) {
        Map<String, String> fields = new TreeMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return body("VALIDATION_FAILED", "Request has invalid fields", fields);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> notFound(AccountNotFoundException e) {
        return body("ACCOUNT_NOT_FOUND", e.getMessage(), null);
    }

    @ExceptionHandler(TransferRejectedException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, Object> rejected(TransferRejectedException e) {
        return body(e.getCode(), e.getMessage(), null);
    }

    private static Map<String, Object> body(String code, String message, Map<String, String> fields) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", code);
        body.put("message", message);
        if (fields != null) {
            body.put("fields", fields);
        }
        return body;
    }
}
