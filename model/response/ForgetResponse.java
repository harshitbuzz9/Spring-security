package com.bridge.herofincorp.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class ForgetResponse {
    @JsonProperty("Status")
    private String Status;
    @JsonProperty("Message")
    private String Message;
}
