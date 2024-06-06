package com.bridge.herofincorp.repository;

import com.bridge.herofincorp.model.entities.DisbursalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisbursalDetailsRepository extends JpaRepository<DisbursalDetails, Long> {

    @Query(value = "SELECT * from {h-schema}disbursement_data_new where sfdc_login_date between cast(?1 as date) and cast(?2 as date) "+
            "and dealer_code=?3 " +
            "and product_code=?4"
            ,nativeQuery = true)
    List<DisbursalDetails> getDisbursalDetails(String startDate, String endDate, String dealerCode, String product);

    @Query(value = "SELECT * from {h-schema}disbursement_data_new where cast(sfdc_login_date as date) = cast(?1 as date) "+
            "and dealer_code=?2 " +
            "and product_code=?3"
            ,nativeQuery = true)
    List<DisbursalDetails> getDisbursalDetailsDatewise(String date, String dealerCode, String product);
}