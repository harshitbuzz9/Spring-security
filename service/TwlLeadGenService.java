package com.bridge.herofincorp.service;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.model.dto.JourneyIngestionDTO;
import com.bridge.herofincorp.model.request.TwlRequest;
import com.bridge.herofincorp.model.response.CustomerAddressDetailResponse;

import java.text.ParseException;
import java.util.List;

public interface TwlLeadGenService {
    CustomerAddressDetailResponse getAddressInfoFromPincode(Long pinCode);
    List getAssetInfoFromStateCode(String model, String stateCode);
    List getModels(String stateCode);
    JourneyIngestionDTO createIngestionJourney(String token, TwlRequest request, APILogger logger) throws ParseException;
}
