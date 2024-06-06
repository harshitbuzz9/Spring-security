package com.bridge.herofincorp.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor@Builder
@Entity
@Table(name = "lead_generation")
public class LeadGeneration {
    @Id
    private String leadId;
    private String journeyId;
    private Boolean consentStatus;
    private String leadStatus;
    private String dealerCode;
    private Long staffId;
    private String productCode;
    private String offerCode;
    private String panNumberIndividual;
    private String panNumberCompany;
    private String companyName;
    private LocalDate dateOfIncorporation;
    private String companyAddress;
    private String constitution;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Integer ageYrs;
    private String gender;
    private String mobileNumber;
    private String residenceAddress;
    private String contactDetails;
    private String propertyAddress;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String landmark;
    private Long pincode;
    private String city;
    private String state;
    private String model;
    private Double loanAmount;
    private Integer loanTenure;
    private String ownerName;
    private String residenceType;
    private String occupationType;
    private Integer mthsInCurrentBiz;
    private Integer yrsInCurrentBiz;
    private Integer totalMthsWorkExp;
    private Integer totalYrsWorkExp;
    private Timestamp created;
    private Timestamp updated;

}
