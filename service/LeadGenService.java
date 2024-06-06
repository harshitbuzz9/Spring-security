package com.bridge.herofincorp.service;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.model.request.PanRequest;
import com.bridge.herofincorp.model.response.PanResponse;

public interface LeadGenService {
    PanResponse getPanDetails(PanRequest request, APILogger logger);
    Object getPinCodeInfo(String pinCode, String product, APILogger logger);
}