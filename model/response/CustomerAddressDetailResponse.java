package com.bridge.herofincorp.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class CustomerAddressDetailResponse {
    private Long pinCode;
    private String state;
    private String city;
    private String district;
    private String village;
    private String office;
    private String stateCode;
}
