package com.bridge.herofincorp.service.impls;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.configs.SmsConfig;
import com.bridge.herofincorp.exceptions.DataFetchException;
import com.bridge.herofincorp.exceptions.OtpException;
import com.bridge.herofincorp.model.dto.AclOtpDTO;
import com.bridge.herofincorp.model.dto.OTPResponseDTO;
import com.bridge.herofincorp.model.entities.LeadGeneration;
import com.bridge.herofincorp.model.request.ConsentRequest;
import com.bridge.herofincorp.model.request.LeadGenerationRequest;
import com.bridge.herofincorp.model.request.SoftpullRequest;
import com.bridge.herofincorp.model.response.ConsentResponse;
import com.bridge.herofincorp.model.response.LeadGenerationResponse;
import com.bridge.herofincorp.repository.LeadGenerationRepository;
import com.bridge.herofincorp.service.AssociateService;
import com.bridge.herofincorp.service.OtpService;
import com.bridge.herofincorp.service.UclLeadGenService;
import com.bridge.herofincorp.utils.ApplicationConstants;
import com.bridge.herofincorp.utils.LeadGenerationMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.coyote.BadRequestException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UclLeadGenServiceImpl implements UclLeadGenService {
    private final String baseUrl = "https://services-qa.herofincorp.com/api/v1/master/";
    @Autowired
    private WebClient webClient;

    private final SmsConfig smsConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private LeadGenerationRepository leadGenerationRepository;
    @Autowired
    private JWTTokenService tokenService;
    @Autowired
    private AssociateService associateService;
    @Autowired
    private OtpService messageService;

    public UclLeadGenServiceImpl(SmsConfig smsConfig) {
        this.smsConfig = smsConfig;
    }

    @Override
    public LeadGenerationResponse sendConsentSMS(LeadGenerationRequest request, String token) throws Exception {

        String applicationId = request.getPan()+LocalDateTime.now();
        request.setApplicationId(applicationId);
        AclOtpDTO dto = messageService.createPayload(Long.valueOf(request.getMobileNumber()),"Dear Customer, Click on https://www.herofincorp.com for Major Terms & Conditions for your Loan No. test. Hero Fincorp");
        ResponseEntity<OTPResponseDTO> response  = messageService.sendMessage(dto);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            JWTClaimsSet claims = tokenService.verifySignature(token).getJWTClaimsSet();
            String userType = claims.getClaim("userType").toString();
            String userId = claims.getSubject();
            String dealerCode = "";
            if(userType.equals(ApplicationConstants.USER_TYPE_DEALER)){
                dealerCode=userId;
            }
            if(userType.equals(ApplicationConstants.USER_TYPE_STAFF)){
                dealerCode = associateService.getAssociateById(Integer.parseInt(userId),null).getDealerCode();
            }
            return LeadGenerationMapper.mapLeadToLeadGenerationResponse(leadGenerationRepository.save(LeadGenerationMapper.mapRequestToLeadGenerationDetails(request,dealerCode)));
        } else {
            throw new OtpException("Unable to send OTP..!!"+ response);
        }
    }

    @Override
    public ConsentResponse checkConsentStatus(String leadId) throws BadRequestException {
        Optional<LeadGeneration> lead = leadGenerationRepository.findByLeadId(leadId);
        if(lead.isPresent()){
        return ConsentResponse.builder()
                 .leadId(lead.get().getLeadId())
                 .consentStatus(lead.get().getConsentStatus())
                 .build();
        }else {
            throw new BadRequestException("Lead not present");
        }
    }

    @Override
    public ConsentResponse updateConsentStatus(ConsentRequest request) throws BadRequestException {
        Optional<LeadGeneration> lead = leadGenerationRepository.findByLeadId(request.getLeadId());
            if (lead.isPresent()) {
                lead.get().setConsentStatus(request.isConsentStatus());
                leadGenerationRepository.save(lead.get());
                return ConsentResponse.builder()
                        .leadId(request.getLeadId())
                        .consentStatus(lead.get().getConsentStatus())
                        .build();
            } else {
                throw new BadRequestException("Lead not present");
            }
    }

    @Override
    public Object getDealerInfo(String dealerCode, String product, APILogger logger) {
        logger.add("UclLeadGenService.getDealerInfo()-startTime: "+ LocalDateTime.now(),"");
        final String url = baseUrl+product+"/dealercode";
        Object response;
        try{
            logger.add("fetching dealer code information with dealerCode: "+dealerCode,"");
            response = webClient.get()
                    .uri(url+"/"+dealerCode)
                    .header("Authorization","Basic bXdfdXNlcl91YXQ6SEZDQDA5ODch")
                    .retrieve()
                    .bodyToMono(Object.class).block();
            logger.add("fetched info: "+response,"");
        }catch(Exception e){
            logger.logError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            throw new DataFetchException("external api for getting dealer code information from getDealerInfo() in ucl service");
        }
        logger.add("UclLeadGenService.getDealerInfo()-endTime: "+ LocalDateTime.now(),"");
        return response;
    }

    @Override
    public Object getPartnerOffers(SoftpullRequest request, APILogger logger) {
        logger.add("UclLeadGenService.getPartnerOffers()-startTime: "+ LocalDateTime.now(),"");
        final String url = "https://api-dev.sb.herofincorp.com/offersapi/v1/sync/partner-offers";
        Object response;
        try{
            logger.add("fetching partner offers information","");
            response = webClient.post()
                    .uri(url)
                    .header("Authorization","Basic bXdfdXNlcl91YXQ6SEZDQDA5ODch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Object.class).block();
            logger.add("fetched info: "+response,"");
        }catch(Exception e){
            logger.logError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            throw new DataFetchException("external api for getting offers information from getPartnerOffers() in ucl service");
        }
        logger.add("UclLeadGenService.getPartnerOffers()-endTime: "+ LocalDateTime.now(),"");
        return response;
    }
}