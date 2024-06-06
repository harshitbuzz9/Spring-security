package com.bridge.herofincorp.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class InvoiceRequest {
    private String dealerCode;
    private String invoiceId;
}
