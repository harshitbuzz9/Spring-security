package com.bridge.herofincorp.model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsentResponse {

    private String leadId;
    private boolean consentStatus;
}
