package com.bridge.herofincorp.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
@Entity
@Table(name = "disbursement_data_new")
public class DisbursalDetails {
    @Id
    private Long applicationId;
    private String dealerCode;
    private Timestamp sfdcLoginDate;
    private String sfdcApplicationId;
    private String customerName;
    private String applicationStatus;
    private String utrNumber;
    private Double disbursedAmount;
    private Timestamp disbursedDate;
    private String productCode;
    private String pan;
    private String model;
    private String scheme;
    private Timestamp lastUpdatedDate;
}
