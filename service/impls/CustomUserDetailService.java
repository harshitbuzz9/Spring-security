package com.bridge.herofincorp.service.impls;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.model.dto.CustomUserDetails;
import com.bridge.herofincorp.model.response.AssociateResponse;
import com.bridge.herofincorp.model.response.DealerResponse;
import com.bridge.herofincorp.service.AssociateService;
import com.bridge.herofincorp.service.DealerService;
import com.bridge.herofincorp.utils.ApplicationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private AssociateService associateService;
    @Autowired
    private DealerService dealerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        APILogger logger = new APILogger("customUserDetailService", LocalDateTime.now().toString());
        if (username.split("&")[0].equals(ApplicationConstants.USER_TYPE_DEALER)){
            logger.add("getProfileInformation-startTime",LocalDateTime.now().toString());
            DealerResponse dealer = dealerService.getProfileInformation(username.split("&")[1]);
            logger.add("getProfileInformation-endTime",LocalDateTime.now().toString());
            return new CustomUserDetails(dealer.getUsername(), List.of(ApplicationConstants.ROLE_ADMIN));
        } else if (username.split("&")[0].equals(ApplicationConstants.USER_TYPE_STAFF)) {
            logger.add("getAssociateById-startTime",LocalDateTime.now().toString());
            AssociateResponse response = associateService.getAssociateById(Integer.valueOf(username.split("&")[1]), logger);
            logger.add("getAssociateById-endTime",LocalDateTime.now().toString());
            return new CustomUserDetails(response.getStaffId().toString(),response.getRole());
        }
        return new CustomUserDetails(null,null);
    }
}