package com.bridge.herofincorp.service.impls;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.exceptions.DataFetchException;
import com.bridge.herofincorp.model.dto.JourneyIngestionDTO;
import com.bridge.herofincorp.model.entities.LeadGeneration;
import com.bridge.herofincorp.model.request.TwlRequest;
import com.bridge.herofincorp.model.response.*;
import com.bridge.herofincorp.repository.LeadGenerationRepository;
import com.bridge.herofincorp.service.AssociateService;
import com.bridge.herofincorp.service.TwlLeadGenService;
import com.bridge.herofincorp.utils.ApplicationConstants;
import com.bridge.herofincorp.utils.ApplicationUtilsData;
import com.bridge.herofincorp.utils.LeadGenerationMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import org.modelmapper.ModelMapper;
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
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class TwlLeadGenServiceImpl implements TwlLeadGenService {
    private static final Logger log = LoggerFactory.getLogger(TwlLeadGenServiceImpl.class);
    private final String baseUrl = "https://festive.api.herofincorp.com/v1/";
    @Autowired
    private WebClient webClient;
    @Autowired
    private LeadGenerationRepository leadGenerationRepository;
    @Autowired
    private JWTTokenService jwtService;
    @Autowired
    private AssociateService associateService;
    @Autowired
    private ModelMapper mapper;

    @Override
    public CustomerAddressDetailResponse getAddressInfoFromPincode(Long pinCode) {
        CustomerAddressDetailResponse response = null;
        String url = baseUrl+"customer/pin/"+pinCode;
        try {
            log.info("calling external api from getAddressInfoFromPincode() in twl service");
            response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(CustomerAddressDetailResponse.class).block();
        }catch (Exception e){
            log.error("error while calling external api from getAddressInfoFromPincode() in twl service: {}",e.getMessage());
            throw new DataFetchException("external api from getAddressInfoFromPincode() in twl service: "+pinCode);
        }
        return response;
    }

    @Override
    public List getAssetInfoFromStateCode(String model, String stateCode) {
        List<LinkedHashMap<Object,Object>> list;
        String url = baseUrl+"asset/"+ApplicationUtilsData.getUrlSpacesReplaceBySpecialCode(model)+"/"+stateCode;
        try {
            log.info("calling external api from getAssetInfoFromStateCode() in twl service");
            list = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(List.class).block();
            if (list!=null) list
                    .forEach(x->x.put("maxAmount",(Double.parseDouble(x.get("orp").toString())*90)/100));
        }catch (Exception e){
            log.error("error while calling external api from getAssetInfoFromStateCode() in twl service: {}",e.getMessage());
            throw new DataFetchException("external api from getAssetInfoFromStateCode() in twl service: with model: "+model+" and state code: "+stateCode);
        }
        return list;
    }

    @Override
    public List getModels(String stateCode) {
        List<LinkedHashMap<Object, Object>> response;
        String url = "https://festive-dev.sb.herofincorp.com/v1/asset";
        try {
            log.info("calling external api from getModels() in twl service");
            response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(List.class).block();

        }catch (Exception e){
            log.error("error while calling external api from getModels() in twl service: {}",e.getMessage());
            throw new DataFetchException("external api from getModels() in twl service with stateCode: "+stateCode);
        }
        return response.stream()
                .filter(x->x.get("state").toString().equals(stateCode)).toList();
    }

    @Override
    public JourneyIngestionDTO createIngestionJourney(String token, TwlRequest request, APILogger logger) throws ParseException {
        AccessTokenResponse tokenResponse;
        JourneyIngestionDTO response;
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", "53q9dp91b1ubqb84t8qglsjlel");
        formData.add("client_secret", "404h97f7logt1ungs1v9nqlc736d4af3nev8afsr92oattq0pnr");
        formData.add("grant_type", "client_credentials");
        formData.add("scope", "com.herofincorp-qa.oauth.b2b/update");
        String url = "https://elm-qa.auth.ap-south-1.amazoncognito.com/oauth2/token";
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
                    .uri("https://elm-qa.api.sb.herofincorp.com/v1/source/journey-ingestion")
                    .header("Authorization", "Bearer "+tokenResponse.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(JourneyIngestionDTO.class).block();
        }catch (Exception e){
            log.error("error while calling external api for getting Twl Details from createIngestionJourney() in twl service: {}",e);
            throw new DataFetchException("external api for getting Twl Details from createIngestionJourney() in twl service");
        }
        if (response!=null){
            log.info("creating Lead Generation object for leadId: {}",response.getData().getJourneyId());
            LeadGeneration lead = LeadGenerationMapper.mapJourneyRequestToLeadGenerationDetails(request,response);
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