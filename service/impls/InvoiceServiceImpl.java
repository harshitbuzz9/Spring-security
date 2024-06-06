package com.bridge.herofincorp.service.impls;

import com.bridge.herofincorp.exceptions.DataFetchException;
import com.bridge.herofincorp.model.request.InvoiceRequest;
import com.bridge.herofincorp.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    @Autowired
    private WebClient webClient;
//    @Value("${app.dealer-login.uat.base-url}")
//    private String baseUrl;
private final String baseUrl = "https://10.50.250.184:443/api/v1/dealer/";
    @Value("${app.dealer-login.api.key}")
    private String apiKey;

    @Override
    public Object getInvoiceByDateRange(String dealerCode, String startDate, String endDate) {
        Object response = null;
        try {
            log.info("calling external api for getting invoice details");
            response = webClient.get()
                    .uri(baseUrl+dealerCode+"/invoices/"+"?startdate="+startDate+"&enddate="+endDate+"&state=all")
                    .header("X-API-Key", apiKey)
                    .retrieve()
                    .bodyToMono(Object.class).block();
        }catch (Exception e){
            log.error("error while calling dealer invoice API: {}",e.getMessage());
            throw new DataFetchException("dealer invoice API with dealerCode: "+dealerCode);
        }
        return response;
    }

    @Override
    public Object getInvoiceById(InvoiceRequest request) {
        Object response = null;
        String invoice = Base64.getEncoder().encodeToString(request.getInvoiceId().getBytes());
        String dealerCode = request.getDealerCode();
        try {
            log.info("calling external api for getting invoice details");
            response = webClient.get()
                    .uri(baseUrl+dealerCode+"/invoiceById/"+invoice)
                    .header("X-API-Key", apiKey)
                    .retrieve()
                    .bodyToMono(Object.class).block();
        }catch (Exception e){
            log.error("error while calling dealer invoice API: {}",e);
            throw new DataFetchException("dealer invoice API with dealerCode: "+dealerCode);
        }
        return response;
    }
}
