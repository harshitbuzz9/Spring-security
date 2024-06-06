package com.bridge.herofincorp.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class DisbursalResponse {
    private String dealerCode;
    private String groupBy;
    private List<DisbursalDetailsResponse> months;
}
