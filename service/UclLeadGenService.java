package com.bridge.herofincorp.service;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.model.request.ConsentRequest;
import com.bridge.herofincorp.model.request.LeadGenerationRequest;
import com.bridge.herofincorp.model.request.SoftpullRequest;
import com.bridge.herofincorp.model.response.ConsentResponse;
import com.bridge.herofincorp.model.response.LeadGenerationResponse;
import org.apache.coyote.BadRequestException;

public interface UclLeadGenService {

    LeadGenerationResponse sendConsentSMS(LeadGenerationRequest request, String token) throws Exception;

    ConsentResponse checkConsentStatus(String LeadId) throws BadRequestException;

    ConsentResponse updateConsentStatus(ConsentRequest request) throws BadRequestException;

    Object getDealerInfo(String dealerCode, String product, APILogger logger);

    Object getPartnerOffers(SoftpullRequest request, APILogger logger);
}
