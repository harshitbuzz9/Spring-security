package com.bridge.herofincorp.model.response;


import lombok.Data;

@Data
public class GenerateOtpResponse {

    private String message;
    private DataResponse data;
    private String status;
    private int statusCode;

}
