package com.bridge.herofincorp.controller;

import com.bridge.herofincorp.model.request.DealerUpdateRequest;
import com.bridge.herofincorp.model.response.PartnerResponse;
import com.bridge.herofincorp.service.DealerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/bridge-app/dealer")
public class DealerController {
    @Autowired
    private DealerService service;

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/update/{dealerCode}")
    public PartnerResponse updateDealerInfo(@PathVariable String dealerCode,
                                            @RequestBody DealerUpdateRequest request){
        return service.updateDealerInformation(dealerCode,request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{dealerCode}")
    public PartnerResponse getDealerInfo(@PathVariable String dealerCode){
        return service.getDealerInfo(dealerCode);
    }
}
