package com.bridge.herofincorp.service.impls;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.exceptions.DataFetchException;
import com.bridge.herofincorp.model.entities.LeadGeneration;
import com.bridge.herofincorp.model.request.LeadGenerationRequest;
import com.bridge.herofincorp.model.response.AccessTokenResponse;
import com.bridge.herofincorp.model.response.AssociateResponse;
import com.bridge.herofincorp.model.response.LapELMApiResponse;
import com.bridge.herofincorp.repository.LeadGenerationRepository;
import com.bridge.herofincorp.service.AssociateService;
import com.bridge.herofincorp.service.LeadIngestionService;
import com.bridge.herofincorp.utils.ApplicationConstants;
import com.bridge.herofincorp.utils.LeadGenerationMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.ParseException;
import java.time.LocalDate;

@Service
public class LeadIngestionServiceImpl implements LeadIngestionService {

    private static final Logger log = LoggerFactory.getLogger(TwlLeadGenServiceImpl.class);
    @Autowired
    private WebClient webClient;
    @Autowired
    private LeadGenerationRepository leadGenerationRepository;
    @Autowired
    private JWTTokenService jwtService;
    @Autowired
    private AssociateService associateService;

    @Override
    public LapELMApiResponse createIngestionJourney(String token, LeadGenerationRequest request, APILogger logger) throws ParseException {
        AccessTokenResponse tokenResponse;
        LapELMApiResponse response;
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", "3MVG9duA.sPEm7m.x.1kr7SEvoe6fM9F1gT.Tg8lEs84C8QcsiwJLKp2n5yjQ40RlkX3cnegJV1BMl8cXyncz");
        formData.add("client_secret", "450D8DDA1E0F4D5383D004B3E06AE15DD45FA3A84CF9FBA82669A0279552B4B6");
        formData.add("grant_type", "client_credentials");
//        formData.add("scope", "com.herofincorp-qa.oauth.b2b/update");
        String url = "https://hfclcorp--situat.sandbox.my.salesforce.com/services/oauth2/token?grant_type=client_credentials";
        try {
            log.info("calling external api for getting access_token from createIngestionJourney() in twl service");
            tokenResponse = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(AccessTokenResponse.class).block();
        }catch (Exception e){
            log.error("error while calling external api for getting access_token from createIngestionJourney() in twl service: {}",e.getMessage());
            throw new DataFetchException("external api for getting access_token from createIngestionJourney() in twl service");
        }
        try {
            log.info("calling external api for getting Twl Details from createIngestionJourney() in twl service");
            response = webClient.post()
                    .uri("https://hfclcorp--situat.sandbox.my.salesforce.com/services/data/v56.0/sobjects/Lead")
                    .header("Authorization", "Bearer "+tokenResponse.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(LeadGenerationMapper.mapLeadGenerationRequestToLapRequest(request))
                    .retrieve()
                    .bodyToMono(LapELMApiResponse.class).block();
        }catch (Exception e){
            log.error("error while calling external api for getting Twl Details from createIngestionJourney() in twl service: {}",e);
            throw new DataFetchException("external api for getting Twl Details from createIngestionJourney() in twl service");
        }
        if (response!=null){
            LocalDate leadDateTime = LocalDate.parse(LocalDate.now().toString().substring(0,10));
            log.info("creating Lead Generation object for leadId: {}",response.getId());
            LeadGeneration lead = LeadGenerationMapper.mapJourneyRequestToLAPLeadGenerationDetails(request,response);
            try{
                log.info("getting userType and user id information from jwt token");
                JWTClaimsSet claims = jwtService.verifySignature(token).getJWTClaimsSet();
                String userType = claims.getClaim("userType").toString();
                String subject = claims.getSubject();
                log.info("got userType:{} and user id:{} information from jwt token",userType,subject);
                if (userType.equals(ApplicationConstants.USER_TYPE_STAFF)){
                    log.info("getting staff data from associate service with staffId: {}",subject);
                    AssociateResponse associate = associateService.getAssociateById(Integer.valueOf(subject), logger);
                    lead.setStaffId(Long.valueOf(subject));
                    lead.setDealerCode(associate.getDealerCode());
                }
                if (userType.equals(ApplicationConstants.USER_TYPE_DEALER)) {
                    lead.setDealerCode(subject);
                }
                try{
                    log.info("saving lead generation information in database with leadId: {}",lead.getLeadId());
                    leadGenerationRepository.save(lead);
                }catch(Exception e){
                    log.error("error during saving lead generation info to database in create ingestion journey: {}",e);
                    throw e;
                }
            }catch (Exception e){
                log.error("error during getting userType and userId from jwt token in create ingestion journey: {}",e);
                throw e;
            }
        }
        return response;
    }
}
