package com.bridge.herofincorp.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadGenerationRequest {

//    @NotNull
//    @NotBlank
    private String applicationId;
    private String product;
    private String mobileNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dob;
    private String pan;
    private String permanentResiAddLine1;
    private String permanentResiAddLine2;
    private String permanentResiAddLine3;
    private Double loanAmount;
    private int tenure;
    private String state;
    private String city;
    private long pinCode;
    private String landmark;
    private String gender;
    private int age;
    private String companyPan;
    private String companyName;
    private String doIncorporation;
    private String companyAddress;
    private String constitution;
    private String propertyAddress;
    private String ownerId;
    private String source;

}
