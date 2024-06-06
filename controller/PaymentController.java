package com.bridge.herofincorp.controller;

import com.bridge.herofincorp.model.response.PaymentResponse;
import com.bridge.herofincorp.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/bridge-app/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{dealerCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentResponse getPaymentDetails(@PathVariable String dealerCode,
                                             @RequestParam String groupBy,
                                             @RequestParam String startDate,
                                             @RequestParam String endDate){
        return paymentService.getPaymentDetails(dealerCode,groupBy, startDate, endDate);
    }
}
