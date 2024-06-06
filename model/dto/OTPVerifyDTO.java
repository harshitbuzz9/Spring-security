package com.bridge.herofincorp.model.dto;

import com.bridge.herofincorp.model.response.AssociateResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true, chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OTPVerifyDTO {
    @JsonProperty("associate_data")
    private AssociateResponse associate;
    @JsonProperty("access_token")
    private String access_token;
    @JsonProperty("expires_in")
    private int expires_in;
    @JsonProperty("token_type")
    private String token_type;
}
