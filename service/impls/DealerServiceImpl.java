package com.bridge.herofincorp.service.impls;

import com.bridge.herofincorp.exceptions.DataFetchException;
import com.bridge.herofincorp.exceptions.DealerNotFoundException;
import com.bridge.herofincorp.model.dto.ProductDto;
import com.bridge.herofincorp.model.entities.Dealer;
import com.bridge.herofincorp.model.request.DealerUpdateRequest;
import com.bridge.herofincorp.model.request.PartnerLoginRequest;
import com.bridge.herofincorp.model.request.PartnerRequest;
import com.bridge.herofincorp.model.response.DealerResponse;
import com.bridge.herofincorp.model.response.ForgetResponse;
import com.bridge.herofincorp.model.response.PartnerResponse;
import com.bridge.herofincorp.repository.DealerRepository;
import com.bridge.herofincorp.service.DealerService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

@Service
public class DealerServiceImpl implements DealerService {
    @Autowired
    private DealerRepository dealerRepository;
    @Autowired
    private WebClient webClient;
    @Autowired
    private ModelMapper mapper;

//    @Value("${app.dealer-login.uat.base-url}")
private final String baseLoginUrl = "https://10.50.250.184:443/api/v1/dealer/";
    @Value("${app.dealer-login.api.key}")
    private String apiKey;
    private static final Logger log = LoggerFactory.getLogger(DealerServiceImpl.class);
    @Override
    public DealerResponse getLogin(PartnerLoginRequest request) {
        PartnerRequest partnerRequest = new PartnerRequest(request.getUsername(),request.getPassword());
        DealerResponse response = new DealerResponse();
        try {
            log.info("calling external api for dealer login");
            response = webClient.post()
                    .uri(baseLoginUrl+"login")
                    .header("X-API-Key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(partnerRequest)
                    .retrieve()
                    .bodyToMono(DealerResponse.class).block();
        }catch (Exception e){
            log.error("error while calling dealer login API: {}",e.getMessage());
            throw new DataFetchException("dealer login API with dealerCode: "+request.getUsername());

        }
        if (response!=null){
            Dealer dealer = Dealer.builder()
                    .name(response.getDealerName())
                    .dealerCode(response.getDealerCode())
                    .email(response.getEmailId())
                    .onboardingDate(LocalDate.parse(response.getVendorCreationDate()!=null?response.getVendorCreationDate():"2012-01-01"))
                    .product(response.getProducts().stream().map(ProductDto::getName).toList().toString()
                            .replace("[","").replace("]",""))
                    .created(new Timestamp(System.currentTimeMillis()))
                    .updated(new Timestamp(System.currentTimeMillis()))
                    .build();
            try {
                log.info("inserting dealer info from dealer login");
                Dealer check = dealerRepository.findByDealerCode(response.getDealerCode());
                if (check==null) dealerRepository.save(dealer);
            }catch (Exception e){
                log.error("error while calling inserting dealer info: {}",e.getMessage());
                throw new DataFetchException("inserting dealer info with dealerCode: "+response.getDealerCode());
            }
        }
        return response;
    }

    @Override
    public ForgetResponse forgetPassword(String dealerCode) {
        ForgetResponse response = new ForgetResponse();
        String url = baseLoginUrl+dealerCode+"/forgotPassword";
        try {
            log.info("calling external api for dealer forget password with dealer code: {}",dealerCode);
            response = webClient.post()
                    .uri(url)
                    .header("X-API-Key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(ForgetResponse.class).block();
        }catch (Exception e){
            log.error("error while calling dealer forget password API: {}",e.getMessage());
            throw new DataFetchException("dealer forget password API with dealerCode: "+dealerCode);
        }
        return response;
    }

    @Override
    public DealerResponse getProfileInformation(String dealerCode) {
        DealerResponse response = new DealerResponse();
        try {
            log.info("calling external api for fetching dealer profile information for dealer code: {}",dealerCode);
            response = webClient.get()
                    .uri(baseLoginUrl+ "{dealerCode}"+"/profile",dealerCode)
                    .header("X-API-Key", apiKey)
                    .retrieve()
                    .bodyToMono(DealerResponse.class).block();
        }catch (Exception e){
            log.error("error while fetching dealer details profile API: {}",e);
            throw new DataFetchException("dealer details profile API with dealerCode: "+dealerCode);
        }
        return response;
    }

    @Override
    public PartnerResponse updateDealerInformation(String dealerCode, DealerUpdateRequest request) {
        Dealer dealer;
        try {
            log.info("calling dealerRepository for fetching dealer profile information to update for dealer code: {}",dealerCode);
            dealer = dealerRepository.findByDealerCode(dealerCode);
            if(dealer==null) throw new DealerNotFoundException("Dealer doesn't exist with dealerCode: "+dealerCode);
        }catch (Exception e){
            log.error("error while fetching dealer details profile in dealerRepository: {}",e);
            throw new DataFetchException("dealer details profile API with dealerCode: "+dealerCode);
        }
        dealer.setBirthday(request.getBirthday());
        dealer.setSpouseName(request.getSpouseName());
        dealer.setMarriageAnniversary(request.getMarriageAnniversery());
        dealer.setSpouseBirthday(request.getSpouseBirthday());
        dealer.setNoOfChildren(request.getNoOfChildren());
        dealer.setUpdated(Timestamp.from(Instant.ofEpochSecond(System.currentTimeMillis())));
        try {
            log.info("updating dealer info for dealer code: {}",dealerCode);
            dealerRepository.save(dealer);
        }catch (Exception e){
            log.error("error while fetching dealer details profile in dealerRepository: {}",e);
            throw new DataFetchException("dealer details profile API with dealerCode: "+dealerCode);
        }
        return mapper.map(dealer,PartnerResponse.class);
    }

    @Override
    public PartnerResponse getDealerInfo(String dealerCode) {
        Dealer dealer;
        try {
            log.info("calling dealerRepository for fetching dealer profile information for dealer code: {}",dealerCode);
            dealer = dealerRepository.findByDealerCode(dealerCode);
            if(dealer==null) throw new DealerNotFoundException("Dealer doesn't exist with dealerCode: "+dealerCode);
        }catch (Exception e){
            log.error("error while fetching dealer details profile in dealerRepository: {}",e);
            throw new DataFetchException("dealer details profile API with dealerCode: "+dealerCode);
        }
        return mapper.map(dealer,PartnerResponse.class);
    }
}