package com.bridge.herofincorp.model.response;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VerifyOtpResponse {

    private String message;
    private TokenDataResponse data;
    private String status;
    private int statusCode;

}
