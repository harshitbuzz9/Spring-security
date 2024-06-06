package com.bridge.herofincorp.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DealerInfoDetailResponse {
    @JsonProperty("RM")
    private List<DealerDetailResponse> RM;
    @JsonProperty("RSM")
    private List<DealerDetailResponse> RSM;
    @JsonProperty("ASM")
    private List<DealerDetailResponse> ASM;
    @JsonProperty("NSM")
    private List<DealerDetailResponse> NSM;
}
