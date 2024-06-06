package com.bridge.herofincorp.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "se_survey")
public class Survey {
    @Id
    private String surveyQuestionId;
    private String productCode;
    private String surveyCategory;
    private String surveyDescription;
    private String surveyQuestion;
    private String created;
    private String updated;
}
