package com.bridge.herofincorp.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PanResponse {
     private String requestId;
     private PanDetailResponse result;
     private Integer statusCode;
     private String applicationId;
}
