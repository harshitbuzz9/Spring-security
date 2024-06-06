package com.bridge.herofincorp.repository;

import com.bridge.herofincorp.model.entities.SeSurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeSurveyResponseRepository extends JpaRepository<SeSurveyResponse, String> {
    @Query(value = "SELECT * from {h-schema}se_survey_response where dealer_code=?1 " +
            "and se_record_id=?2 " +
            "and product_code=?3 "+
            "and survey_month=cast(?4 as date)",nativeQuery = true)
    List<SeSurveyResponse> getSurvey(String dealerCode, String seId, String productCode, String surveyMonth);
}
