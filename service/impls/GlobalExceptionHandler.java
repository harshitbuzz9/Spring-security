package com.bridge.herofincorp.service.impls;

import com.bridge.herofincorp.model.response.ExceptionMessageResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionMessageResponse> handleBadRequestException(BadRequestException ex) {
        ExceptionMessageResponse response = ExceptionMessageResponse.builder()
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessageResponse> handleGeneralException(Exception ex) {
        ExceptionMessageResponse response = ExceptionMessageResponse.builder()
                .message("An error occurred while processing the request")
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

