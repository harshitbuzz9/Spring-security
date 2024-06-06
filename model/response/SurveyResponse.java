package com.bridge.herofincorp.model.response;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SurveyResponse {
    private String surveyQuestionId;
    private String dealerCode;
    private String dealerName;
    private String productCode;
    private String seRecordId;
    private String seName;
    private LocalDate surveyMonth;
    private Integer surveyResponse;
    private Timestamp surveyDatetime;
    private String surveyStatus;
}
