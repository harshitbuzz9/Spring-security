package com.bridge.herofincorp.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyResponseDetails {

    private String dealerCode;
    private String dealerName;
    private String productCode;
    private String seRecordId;
    private String seName;
    private List<SurveyQuestionRequest> questionRequests;
}
