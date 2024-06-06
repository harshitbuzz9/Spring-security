package com.bridge.herofincorp.service.impls;

import com.bridge.herofincorp.exceptions.DataFetchException;
import com.bridge.herofincorp.model.response.PaymentResponse;
import com.bridge.herofincorp.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    @Autowired
    private WebClient webClient;

//    @Value("${app.dealer-login.uat.base-url}")
    private final String baseUrl="https://10.50.250.184:443/api/v1/dealer/";
    @Value("${app.dealer-login.api.key}")
    private String apiKey;

    @Override
    public PaymentResponse getPaymentDetails(String dealerCode, String groupBy, String startDate, String endDate) {
        PaymentResponse response = null;
        try {
            log.info("calling external api for getting payment");
            response = webClient.get()
                    .uri(baseUrl+dealerCode+"/payment"+"?groupby="+groupBy+"&startdate="+startDate+"&enddate="+endDate)
                    .header("X-API-Key", apiKey)
                    .retrieve()
                    .bodyToMono(PaymentResponse.class).block();
        }catch (Exception e){
            log.error("error while calling dealer payment API: {}",e.getMessage());
            throw new DataFetchException("dealer payment API with dealerCode: "+dealerCode);
        }
        return response;
    }
}
