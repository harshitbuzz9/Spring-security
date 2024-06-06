package com.bridge.herofincorp.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PanRequest {
    private String consent;
    private String pan;
    private String product;
    private String source;
    private String applicationId;
}
