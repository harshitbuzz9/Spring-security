package com.bridge.herofincorp.model.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VerifyOtpRequest {
    private String phone;
    private String otp;
    private String appName;
}
