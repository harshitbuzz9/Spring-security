package com.bridge.herofincorp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class PanAddressDto {
    private String buildingName;
    private String locality;
    private String streetName;
    private String pinCode;
    private String city;
    private String state;
    private String country;
}
