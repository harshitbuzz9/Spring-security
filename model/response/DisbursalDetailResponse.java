package com.bridge.herofincorp.model.response;

import lombok.*;

import java.sql.Timestamp;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor@Builder
public class DisbursalDetailResponse {
    private String dealerCode;
    private String lanId;
    private Long appId;
    private String product;
    private String status;
    private String customerName;
    private String utr;
    private DisbursalAmountDetails disbursalDetails;
    private Timestamp loginDate;
    private String model;
    private String scheme;
}
