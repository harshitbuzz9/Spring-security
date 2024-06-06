package com.bridge.herofincorp.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerResponse {
    private Long partnerId;
    private String dealerCode;
    private String email;
    private LocalDate birthday;
    private LocalDate marriageAnniversary;
    private String name;
    private String product;
    private String spouseName;
    private LocalDate spouseBirthday;
    private LocalDate onboardingDate;
    private Integer noOfChildren;
}
