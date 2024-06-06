package com.bridge.herofincorp.service;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.model.dto.JourneyIngestionDTO;
import com.bridge.herofincorp.model.request.LeadGenerationRequest;
import com.bridge.herofincorp.model.request.TwlRequest;
import com.bridge.herofincorp.model.response.LapELMApiResponse;

import java.text.ParseException;

public interface LeadIngestionService {
    LapELMApiResponse createIngestionJourney(String token, LeadGenerationRequest request, APILogger logger) throws ParseException;
}
