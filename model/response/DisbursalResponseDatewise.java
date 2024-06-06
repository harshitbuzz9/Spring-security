package com.bridge.herofincorp.model.response;

import lombok.*;

import java.util.List;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor@Builder
public class DisbursalResponseDatewise {
    private Long applicationId;
    private DisbursalDetailResponse data;
    private String customerName;
    private String applicationStatus;
    private Double disbursedAmount;
}
