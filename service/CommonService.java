package com.bridge.herofincorp.service;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.model.request.SurveyResponseDetails;
import com.bridge.herofincorp.model.response.DealerInfoResponse;
import com.bridge.herofincorp.model.response.QuestionResponse;
import com.bridge.herofincorp.model.response.SurveyResponse;

public interface CommonService {
    QuestionResponse getQuestionsByProductCode(APILogger logger, String productCode);

    DealerInfoResponse getDealerInfoForSurvey(APILogger logger, String dealerCode, String seId, String productCode, String surveyMonth);

    SurveyResponse saveResponse(String token, SurveyResponseDetails request, APILogger logger);

}
