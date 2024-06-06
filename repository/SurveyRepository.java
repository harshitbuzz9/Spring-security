package com.bridge.herofincorp.repository;

import com.bridge.herofincorp.model.entities.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey,String> {
    List<Survey> findAllByProductCode(String productCode);
}
