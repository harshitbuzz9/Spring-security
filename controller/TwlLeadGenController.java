package com.bridge.herofincorp.controller;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.model.dto.JourneyIngestionDTO;
import com.bridge.herofincorp.model.request.TwlRequest;
import com.bridge.herofincorp.model.response.CustomerAddressDetailResponse;
import com.bridge.herofincorp.service.TwlLeadGenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/bridge-app/leadgen/twl")
public class TwlLeadGenController {
    @Autowired
    private TwlLeadGenService service;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{pinCode}")
    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    public CustomerAddressDetailResponse getAddressInfoFromPincode(@PathVariable Long pinCode){
        return service.getAddressInfoFromPincode(pinCode);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/asset/{model}/{stateCode}")
    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    public List getAssetInfoFromStateCode(@PathVariable String model, @PathVariable String stateCode){
        return service.getAssetInfoFromStateCode(model,stateCode);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/models")
    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    public List getModels(@RequestParam String stateCode){
        return service.getModels(stateCode);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/journey-ingestion")
    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    public JourneyIngestionDTO createIngestionJourney(@RequestAttribute("logger")APILogger logger, HttpServletRequest httpRequest, @RequestBody TwlRequest request) throws ParseException {
        String token = httpRequest.getHeader("Authorization").substring(7);
        return service.createIngestionJourney(token, request,logger);
    }
}