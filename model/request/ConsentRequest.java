package com.bridge.herofincorp.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsentRequest {
    private String leadId;
    private boolean consentStatus;
}
