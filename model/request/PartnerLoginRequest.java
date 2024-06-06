package com.bridge.herofincorp.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PartnerLoginRequest {
    private String username;
    private String password;
    private String appName;
}
