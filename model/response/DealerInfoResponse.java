package com.bridge.herofincorp.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class DealerInfoResponse {
    private Boolean dealerServiceable;
    private DealerInfoDetailResponse response;
}
