package com.bridge.herofincorp.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class Response {
    private String message;
    private Object data;
    private HttpStatus status = HttpStatus.OK;
    private Integer statusCode=HttpStatus.OK.value();
}
