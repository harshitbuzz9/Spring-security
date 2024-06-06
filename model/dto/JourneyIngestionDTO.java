package com.bridge.herofincorp.model.dto;

import com.bridge.herofincorp.model.response.JourneyIngestionDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class JourneyIngestionDTO {
    private String message;
    private JourneyIngestionDetailResponse data;
    private String status;
    private Integer statusCode;
}
