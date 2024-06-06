package com.bridge.herofincorp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class JourneyTwlDTO {
    private Double amountRequested;
    private Integer tenureSelected;
    private String subProduct;
    private String dealerCode;
    private String make;
    private String model;
}
