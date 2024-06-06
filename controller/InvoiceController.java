package com.bridge.herofincorp.controller;

import com.bridge.herofincorp.model.request.InvoiceRequest;
import com.bridge.herofincorp.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/bridge-app/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{dealerCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public Object getInvoiceByDateRange(@PathVariable String dealerCode, @RequestParam String startDate, @RequestParam String endDate){
        return invoiceService.getInvoiceByDateRange(dealerCode, startDate, endDate);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/invoiceById")
    @PreAuthorize("hasRole('ADMIN')")
    public Object getInvoiceById(@RequestBody InvoiceRequest request){
        return invoiceService.getInvoiceById(request);
    }
}
