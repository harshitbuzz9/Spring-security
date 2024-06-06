package com.bridge.herofincorp.service;

import com.bridge.herofincorp.model.request.DealerUpdateRequest;
import com.bridge.herofincorp.model.request.PartnerLoginRequest;
import com.bridge.herofincorp.model.response.DealerResponse;
import com.bridge.herofincorp.model.response.ForgetResponse;
import com.bridge.herofincorp.model.response.PartnerResponse;
import org.springframework.web.bind.annotation.PathVariable;

public interface DealerService {
    DealerResponse getLogin(PartnerLoginRequest request);

    ForgetResponse forgetPassword(String dealerCode);

    DealerResponse getProfileInformation(String dealerCode);

    PartnerResponse updateDealerInformation(String dealerCode, DealerUpdateRequest request);
    PartnerResponse getDealerInfo(String dealerCode);
}
