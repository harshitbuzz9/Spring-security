package com.bridge.herofincorp.model.response;

import lombok.*;

import java.time.LocalDate;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor@Builder
public class DisbursalDetailsResponse {
    private String label;
    private LocalDate day;
    private Double amountDisbursed;
    private Double amountCancelled;
    private Integer totalLogin;
    private Integer totalApproved;
    private Integer totalCancelled;
    private Integer totalDisbursed;
}
