package com.bridge.herofincorp.service;

import com.bridge.herofincorp.model.entities.DisbursalDetails;
import com.bridge.herofincorp.model.request.DisbursalRequest;
import com.bridge.herofincorp.model.response.DisbursalDetailResponse;
import com.bridge.herofincorp.model.response.DisbursalResponse;
import com.bridge.herofincorp.model.response.DisbursalResponseDatewise;

import java.util.List;

public interface DisbursalService {
    DisbursalResponse getDisbursal(String startDate, String endDate, String groupBy, DisbursalRequest request);
    List<DisbursalResponseDatewise> getDisbursalDatewise(String date, DisbursalRequest request);
    DisbursalDetailResponse getDisbursalByApplicationId(Long applicationId);
}
