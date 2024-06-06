package com.bridge.herofincorp.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
@Entity
@Builder
@Table(name = "se_survey_response")
public class SeSurveyResponse {
    @Id
    private String id;
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
