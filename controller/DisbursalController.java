package com.bridge.herofincorp.controller;


import com.bridge.herofincorp.model.entities.DisbursalDetails;
import com.bridge.herofincorp.model.request.DisbursalRequest;
import com.bridge.herofincorp.model.response.DisbursalDetailResponse;
import com.bridge.herofincorp.model.response.DisbursalResponse;
import com.bridge.herofincorp.model.response.DisbursalResponseDatewise;
import com.bridge.herofincorp.service.DisbursalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/bridge-app/disbursal")
public class DisbursalController {

    private static final Logger log = LoggerFactory.getLogger(DisbursalController.class);
    @Autowired
    private DisbursalService service;

    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public DisbursalResponse getDisbursalDetails(@RequestParam("startDate")String startDate,
                                    @RequestParam("endDate")String endDate,
                                    @RequestParam("groupBy") String groupBy,
                                    @RequestBody DisbursalRequest request) {
        log.info("calling disbursal service to get disbursal data from {} to {} grouped by {}"
                ,startDate,endDate,groupBy);
        return service.getDisbursal(startDate,endDate,groupBy,request);
    }

    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/daywiseExpansion")
    public List<DisbursalResponseDatewise> getDisbursalDetailsDatewise(@RequestParam("date")String date,
                                                                 @RequestBody DisbursalRequest request){
        log.info("calling disbursal service to get disbursal detail for date: {}",date);
        return service.getDisbursalDatewise(date,request);
    }

    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{applicationId}")
    public DisbursalDetailResponse getDisbursalDetailsDatewise(@PathVariable Long applicationId){
        log.info("calling disbursal service to get disbursal detail for applicationId: {}",applicationId);
        return service.getDisbursalByApplicationId(applicationId);
    }
}
