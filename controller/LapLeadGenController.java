package com.bridge.herofincorp.controller;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.model.request.LeadGenerationRequest;
import com.bridge.herofincorp.model.response.LapELMApiResponse;
import com.bridge.herofincorp.service.LeadIngestionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/bridge-app/leadgen/lap")
public class LapLeadGenController {
    @Autowired
    private LeadIngestionService service;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/journey-ingestion")
    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    public LapELMApiResponse createIngestionJourney(@RequestAttribute("logger")APILogger logger, HttpServletRequest httpRequest, @RequestBody LeadGenerationRequest request) throws ParseException {
        String token = httpRequest.getHeader("Authorization").substring(7);
        return service.createIngestionJourney(token, request,logger);
    }



}
