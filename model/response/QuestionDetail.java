package com.bridge.herofincorp.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class QuestionDetail {
    private String surveyQuestionId;
    private String surveyCategory;
    private String surveyDescription;
    private String surveyQuestion;
}
