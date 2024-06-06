package com.bridge.herofincorp.service;

import com.bridge.herofincorp.model.response.PaymentResponse;
import org.springframework.web.bind.annotation.RequestParam;

public interface PaymentService {
    PaymentResponse getPaymentDetails(String dealerCode, String groupBy, String startDate, String endDate);
}
