package com.bridge.herofincorp.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class RestServiceException extends RuntimeException{
    private HttpStatus statusCode;
    private String error;

    public RestServiceException(

            HttpStatusCode httpStatusCode,
            String error) {

        super();
        this.statusCode = (HttpStatus) httpStatusCode;
        this.error = error;
    }

}
