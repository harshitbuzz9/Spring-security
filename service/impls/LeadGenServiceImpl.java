package com.bridge.herofincorp.service.impls;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.exceptions.DataFetchException;
import com.bridge.herofincorp.model.request.PanRequest;
import com.bridge.herofincorp.model.response.MockPanResponse;
import com.bridge.herofincorp.model.response.PanResponse;
import com.bridge.herofincorp.service.LeadGenService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
public class LeadGenServiceImpl implements LeadGenService {
    @Autowired
    private WebClient webClient;
    @Autowired
    private ModelMapper mapper;
    private final String baseUrl = "https://services-qa.herofincorp.com/api/v1/master/";

    @Override
    public PanResponse getPanDetails(PanRequest request, APILogger logger) {
        final boolean mock = true;
        logger.add("LeadGenServiceImpl.getPanDetails()-startTime"+ LocalDateTime.now(),"");
        String applicationId = request.getPan()+LocalDateTime.now();
        request.setApplicationId(applicationId);
        String url = "https://services-qa.herofincorp.com/api/wrapper/karza/pan-profile";
        PanResponse response;
        try {
            logger.add("fetching pan details with pan no: "+request.getPan(),"");
            if(!mock){
                response = webClient.post()
                        .uri(url)
                        .header("Authorization","Basic bXdfdXNlcl91YXQ6SEZDQDA5ODch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(PanResponse.class).block();
            }else{
                MockPanResponse mockPanResponse = new MockPanResponse();
                response = mapper.map(mockPanResponse,PanResponse.class);
            }
            logger.add("fetched details: "+response,"");
            logger.add("LeadGenServiceImpl.getPanDetails()-endTime"+ LocalDateTime.now(),"");
        }catch (Exception e){
            logger.logError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            throw new DataFetchException("external api from getModels() in twl service with stateCode: ");
        }
        response.setApplicationId(applicationId);
        return response;
    }
    @Override
    public Object getPinCodeInfo(String pinCode, String product, APILogger logger) {
        logger.add("UclLeadGenService.getPinCodeInfo()-startTime: "+ LocalDateTime.now(),"");
        final String url = baseUrl+product+"/pincode";
        Object response;
        try{
            logger.add("fetching pin code information with pincode: "+pinCode,"");
            response = webClient.get()
                    .uri(url+"/"+pinCode)
                    .header("Authorization","Basic bXdfdXNlcl91YXQ6SEZDQDA5ODch")
                    .retrieve()
                    .bodyToMono(Object.class).block();
            logger.add("fetched info: "+response,"");
        }catch(Exception e){
            logger.logError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            throw new DataFetchException("external api for getting pin code information from getPinCodeInfo() in ucl service");
        }
        logger.add("UclLeadGenService.getPinCodeInfo()-endTime: "+ LocalDateTime.now(),"");
        return response;
    }
}
