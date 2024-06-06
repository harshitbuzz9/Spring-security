package com.bridge.herofincorp.exceptions;

import com.bridge.herofincorp.model.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApplicationExceptionHandler implements ResponseErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidArgumentException(MethodArgumentNotValidException ex){
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error->{
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return errorMap;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(PhoneNumberAlreadyExistException.class)
    public ErrorResponse handlePhoneNumberAlreadyExistException(PhoneNumberAlreadyExistException ex){
        return new ErrorResponse(String.valueOf(HttpStatus.CONFLICT.value()), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AssociateNotFoundException.class)
    public ErrorResponse handleAssociateNotFoundException(AssociateNotFoundException ex){
        return new ErrorResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(DatabaseAccessException.class)
    public ErrorResponse handleDatabaseAccessException(DatabaseAccessException ex){
        return new ErrorResponse(String.valueOf(HttpStatus.FORBIDDEN.value()), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DisbursalDetailsNotFoundException.class)
    public ErrorResponse DisbursalDetailsNotFoundException(DatabaseAccessException ex){
        return new ErrorResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataFetchException.class)
    public ErrorResponse handleDataFetchException(DataFetchException ex){
        return new ErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(OtpException.class)
    public ErrorResponse handleOtpException(OtpException ex){
        return new ErrorResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(AssociateNotActiveException.class)
    public ErrorResponse handleAssociateNotActiveException(AssociateNotActiveException ex){
        return new ErrorResponse(String.valueOf(HttpStatus.PRECONDITION_FAILED.value()), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DealerNotFoundException.class)
    public ErrorResponse handleDealerNotFoundException(DealerNotFoundException ex){
        return new ErrorResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), ex.getMessage());
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (
                ((HttpStatus) response.getStatusCode()).series() ==
                        HttpStatus.Series.CLIENT_ERROR

                        || ((HttpStatus) response.getStatusCode()).series() ==
                        HttpStatus.Series.SERVER_ERROR
        );
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is4xxClientError()
                || response.getStatusCode().is5xxServerError()) {


            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getBody()))) {
                String httpBodyResponse = reader.lines()
                        .collect(Collectors.joining(""));

                throw new RestServiceException(
                        response.getStatusCode(),
                        httpBodyResponse);
            }

        }
    }
}
