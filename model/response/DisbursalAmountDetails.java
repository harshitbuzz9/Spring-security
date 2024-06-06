package com.bridge.herofincorp.model.response;

import lombok.*;

import java.time.LocalDate;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor@Builder
public class DisbursalAmountDetails {
    private Double amount;
    private LocalDate date;
}
