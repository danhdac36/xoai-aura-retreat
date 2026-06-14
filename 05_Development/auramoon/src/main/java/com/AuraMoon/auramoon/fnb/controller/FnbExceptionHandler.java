package com.AuraMoon.auramoon.fnb.controller;

import com.AuraMoon.auramoon.fnb.dto.ErrorResponse;
import com.AuraMoon.auramoon.fnb.service.FnbException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FnbExceptionHandler {

    @ExceptionHandler(FnbException.class)
    public ResponseEntity<ErrorResponse> handleFnbException(FnbException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String code = ex.getErrorCode();

        if ("FNB-001".equals(code)) {
            status = HttpStatus.BAD_REQUEST;
        } else if ("FNB-002".equals(code)) {
            status = HttpStatus.FORBIDDEN;
        } else if ("FNB-003".equals(code)) {
            status = HttpStatus.NOT_FOUND;
        } else if ("FNB-004".equals(code)) {
            status = HttpStatus.FORBIDDEN;
        }

        ErrorResponse.ErrorBody body = ErrorResponse.ErrorBody.builder()
                .code(code)
                .message(ex.getMessage())
                .details(ex.getDetails())
                .build();

        ErrorResponse response = ErrorResponse.builder()
                .error(body)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
