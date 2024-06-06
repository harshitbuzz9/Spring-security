package com.bridge.herofincorp.controller;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.model.request.ConsentRequest;
import com.bridge.herofincorp.model.request.LeadGenerationRequest;
import com.bridge.herofincorp.model.request.SoftpullRequest;
import com.bridge.herofincorp.model.response.ConsentResponse;
import com.bridge.herofincorp.model.response.LeadGenerationResponse;
import com.bridge.herofincorp.service.UclLeadGenService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/bridge-app/leadgen/ucl")
public class UclLeadGenController {
    @Autowired
    private UclLeadGenService service;


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/send/consent")
    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    public LeadGenerationResponse sendConsentLink(@RequestBody LeadGenerationRequest request, HttpServletRequest servletRequest) throws Exception {
        String token = servletRequest.getHeader("Authorization").substring(7);
        return service.sendConsentSMS(request,token);
    }



    @GetMapping("/consent/status/{id}")
    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    public ConsentResponse checkConsentStatus(@PathVariable("id") String id) throws BadRequestException {
        return service.checkConsentStatus(id);
    }


    @PatchMapping("/consent/status")
    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    public ConsentResponse updateConsentStatus(@RequestBody ConsentRequest request) throws BadRequestException {
        return service.updateConsentStatus(request);
    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/dealer/{dealerCode}")
    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    public Object getDealerInfo(@RequestAttribute("logger") APILogger logger, @PathVariable String dealerCode, @RequestParam String product){
        Object response;
        try{
            logger.add("UclLeadGenController.getDealerInfo()-startTime"+ LocalDateTime.now(),"");
            response =  service.getDealerInfo(dealerCode,product,logger);
            logger.add("UclLeadGenController.getDealerInfo()-endTime"+ LocalDateTime.now(),"");
            logger.logSuccess(200);
        }catch (Exception e){
            logger.logError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            throw e;
        }
        return response;
    }
    //todo: in dicussion api need to work later
    @PostMapping("/offers")
    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    public Object getPartnerOffers(@RequestAttribute("logger") APILogger logger, @RequestBody SoftpullRequest request){
        Object response;
        try{
            logger.add("UclLeadGenController.getPartnerOffers()-startTime"+ LocalDateTime.now(),"");
            response =  service.getPartnerOffers(request,logger);
            logger.add("UclLeadGenController.getPartnerOffers()-endTime"+ LocalDateTime.now(),"");
            logger.logSuccess(200);
        }catch (Exception e){
            logger.logError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            throw e;
        }
        return response;
    }
}
