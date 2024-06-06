package com.bridge.herofincorp.service;

import com.bridge.herofincorp.model.request.InvoiceRequest;

public interface InvoiceService {
    Object getInvoiceByDateRange(String dealerCode, String startDate, String endDate);

    Object getInvoiceById(InvoiceRequest request);
}
