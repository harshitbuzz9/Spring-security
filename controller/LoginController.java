package com.bridge.herofincorp.controller;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.configs.AuthProperties;
import com.bridge.herofincorp.exceptions.AssociateNotActiveException;
import com.bridge.herofincorp.exceptions.AssociateNotFoundException;
import com.bridge.herofincorp.model.dto.OTPVerifyDTO;
import com.bridge.herofincorp.model.dto.OtpDTO;
import com.bridge.herofincorp.model.request.LoginRequest;
import com.bridge.herofincorp.model.request.LogoutRequest;
import com.bridge.herofincorp.model.request.PartnerLoginRequest;
import com.bridge.herofincorp.model.request.VerifyOtpRequest;
import com.bridge.herofincorp.model.response.*;
import com.bridge.herofincorp.service.AssociateService;
import com.bridge.herofincorp.service.DealerService;
import com.bridge.herofincorp.service.OtpService;
import com.bridge.herofincorp.service.impls.JWTTokenService;
import com.bridge.herofincorp.utils.ApplicationConstants;
import com.bridge.herofincorp.utils.LoginConstant;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/auth/login")
public class LoginController {
    private final JWTTokenService jwtService;
    private final OtpService otpService;
    private final AssociateService associateService;
    private final DealerService dealerService;
    private static final String ACCEPTED_CHARACTERS = "^[-1-9]\\d*$";
    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";
    public LoginController(JWTTokenService jwtService, AuthProperties authProperties, OtpService otpService, AssociateService associateService, AuthenticationProvider authenticationProvider, DealerService dealerService) {
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.associateService = associateService;
        this.dealerService = dealerService;
    }

    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.audience}")
    private String audience;


    //Generate OTP api
    @PostMapping("/generate/otp")
    public ResponseEntity<Response> loginRequestOtp(@RequestBody LoginRequest loginRequest) throws Exception {
        APILogger log = new APILogger("/generate/otp", UUID.randomUUID().toString());
        AssociateResponse associate = associateService.getAssociateByPhone(log,loginRequest.getPhone());
        if (associate==null) throw new AssociateNotFoundException("Associate doesn't exist with mobile number: "+loginRequest.getPhone());
        String status = associate.getStatus();
        if (!status.equalsIgnoreCase(ApplicationConstants.STAFF_STATUS_ACTIVE)) throw new AssociateNotActiveException("Associate Status is not Active with StaffId: "+associate.getStaffId());
        log.add("otp-startTime "+ String.valueOf(LocalDateTime.now()),"");
        String appName = loginRequest.getAppName();
        if(StringUtils.isBlank(appName)) {
            throw new EntityNotFoundException("Application is not configured");
        }

        String otp = otpService.sendOTPSMS(Long.parseLong(loginRequest.getPhone()),appName);
        OtpDTO otpDto = new OtpDTO(otp);
        return getCustomResponse("OTP Sent", otpDto, HttpStatus.ACCEPTED);
    }


    @PostMapping("/verifyOTP")
    public ResponseEntity<Response> verifyRequestOtp(@RequestBody VerifyOtpRequest verifyOtpRequest){
        APILogger log = new APILogger("verifyRequestOtp", UUID.randomUUID().toString());
        log.add("verify-otp-startTime "+ LocalDateTime.now(),"");
        String appName = verifyOtpRequest.getAppName();

        if(StringUtils.isBlank(appName)) {
            throw new EntityNotFoundException("Application is not configured");
        }
        try {
            String mobilenumber = verifyOtpRequest.getPhone();
            String otp = verifyOtpRequest.getOtp();
            log.add("otp "+ otp,"");
            System.out.println("otp = " + otp);
            System.out.println("mobilenumber = " + mobilenumber);
            if (!(mobilenumber.matches(ACCEPTED_CHARACTERS) && otp.matches(ACCEPTED_CHARACTERS))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "mobile number & OTP should be of numerical only");

            }
            if (mobilenumber.length() != 10) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mobile number should be of 10 digits");
            }
            String otpResponse = checkOTP( mobilenumber, otp,appName);
            if (otpResponse.equals(LoginConstant.OTP_VERIFIED)) {
                log.add("Calling getCustomerByPhone ", "");
                AssociateResponse customer = associateService.getAssociateByPhone(log,mobilenumber);
                log.add("LoginController.verifyOTP() customerList.size" + customer, "");
                Integer custId = null;
                List<String> roles = null;
                if (customer != null) {
                    custId = customer.getStaffId();
                    roles = customer.getRole();
                    System.out.println("LoginController.verifyOTP() customer already exists " + custId);
                    log.add("customer already exists " + custId, "");
                } else {
                    log.add("LoginController.verifyOTP() Customer Not Found. Adding new entry custId=" + custId, "");
                    throw new AssociateNotFoundException("Associate doesn't exist with mobile number: "+mobilenumber);
                }
                OTPVerifyDTO verifyOTP = new OTPVerifyDTO();
                verifyOTP.associate(customer);
                verifyOTP.access_token(getJWTToken(custId.toString(),roles, ApplicationConstants.USER_TYPE_STAFF, issuer, appName, 144));
                verifyOTP.expires_in(3600);
                verifyOTP.token_type("bearer");
                return getCustomResponse("Token generated", verifyOTP, HttpStatus.ACCEPTED);
            } else if (otpResponse.equals(LoginConstant.OTP_INVALID)) {
                // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
                return getCustomResponse("Invalid OTP", "", HttpStatus.BAD_REQUEST);
            } else if (otpResponse.equals(LoginConstant.OTP_EXPIRED)) {
                return getCustomResponse("OTP is either Invalid or Expired", "", HttpStatus.BAD_REQUEST);
            } else {
                return getCustomResponse("Invalid Request", "", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.add("verify-otp-Exception", e.getMessage());
            log.add("verify-otp-endTime", String.valueOf(LocalDateTime.now()));
            log.logError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
            throw e;
        }
//        return loginService.verifyOtpRequest(verifyOtpRequest);
    }


    @PostMapping("/getUserData")
    public ResponseEntity<Response> getDealerDetailsData(@RequestHeader("Authorization") String authHeader){
        APILogger log = new APILogger("/userinfo", UUID.randomUUID().toString());
        log.add("userinfo-startTime", String.valueOf(LocalDateTime.now()));
        AssociateResponse authCustomer = null;
        Integer custId = null;
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                // throw new RuntimeException("Unauthorized");
                return getCustomResponse("Unauthorized", "", HttpStatus.BAD_REQUEST);
            }
            String jwtToken = authHeader.substring(7).trim();
            if (verifyToken(jwtToken)) {
                custId = Integer.valueOf(extractSub(jwtToken));
                System.out.println("roles: "+extractRoles(jwtToken));
                if (custId != null) {
                    authCustomer = associateService.getAssociateById(custId, log);
                }
            }
            // JSONObject json = new JsonObject(authCustomer);
            // return authCustomer;
            return getCustomResponse("", authCustomer, HttpStatus.ACCEPTED);
        } catch (RuntimeException e) {
            log.add("Caught under RuntimeException..... ", e.getMessage());
            log.add("userinfo", e.getMessage());
            log.add("userinfo-endTime", String.valueOf(LocalDateTime.now()));
            log.logError("Ivalid JWT", HttpStatus.UNAUTHORIZED.value(), e);
//             throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());

            return getCustomResponse("Ivalid JWT", "", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.add("userinfo-Exception", e.getMessage());
            log.add("userinfo-endTime", String.valueOf(LocalDateTime.now()));
            log.logError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
            throw e;
        }
    }

    @PostMapping("/partner")
    public DealerResponseLogin getDealerDetailsData(@RequestBody PartnerLoginRequest request){
        String appName = request.getAppName();
        if(StringUtils.isBlank(appName)) {
            throw new EntityNotFoundException("Application is not configured");
        }
        APILogger log = new APILogger("forgetPassword", UUID.randomUUID().toString());
        log.add("calling getLogin() of DealerService from DealerController","");
        String token = this.getJWTToken(request.getUsername(), List.of("ADMIN"), ApplicationConstants.USER_TYPE_DEALER, issuer, appName, 86400);
        DealerResponse response =  dealerService.getLogin(request);
        DealerResponseLogin dealerResponseLogin = new DealerResponseLogin();
        if(response!=null){
            dealerResponseLogin.setDealerDetails(response);
            dealerResponseLogin.setAccessToken(token);
        }else{
            throw new RuntimeException("No information found for dealerCode: "+request.getUsername());
        }
        return dealerResponseLogin;
    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/partner/{dealerCode}/forgetPassword")
    public ForgetResponse forgetPassword(@PathVariable String dealerCode){
        APILogger log = new APILogger("forgetPassword", UUID.randomUUID().toString());
        log.add("calling forgetPassword(dealerCode) for dealer code: "+dealerCode+" of DealerService from DealerController","");
        return dealerService.forgetPassword(dealerCode);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/partner/{dealerCode}/profile")
    public DealerResponse getProfile(@PathVariable String dealerCode){
        APILogger log = new APILogger("getProfile", UUID.randomUUID().toString());
        log.add("calling getProfileInformation(dealerCode) for dealer code: "+dealerCode+" of DealerService from DealerController","");
        return dealerService.getProfileInformation(dealerCode);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    public LogoutResponse doLogout(@RequestBody LogoutRequest request) throws ParseException {
        return new LogoutResponse(jwtService.expireToken(request.getToken()));
    }

    private String checkOTP(String mobileNumber, String otp, String appName)
            throws EntityNotFoundException {

        return otpService.verifyOtp(mobileNumber, otp, appName);
    }
    private String getJWTToken(String custId, List<String> roles, String userType, String issuer, String aud, int expirationInMins) {
        APILogger log = new APILogger("getJWTToken", UUID.randomUUID().toString());
        log.add("getJWTToken-startTime", String.valueOf(LocalDateTime.now()));
        SignedJWT jwtGenerated = jwtService.getJWT(custId, roles, userType, issuer, aud, expirationInMins);
        String jwtToken = jwtGenerated.serialize();
        log.add("getJWTToken:jwtToken =" + jwtToken, "");
        log.add("getJWTToken-endTime", String.valueOf(LocalDateTime.now()));
        return jwtToken;
    }

    private ResponseEntity<Response> getCustomResponse(String message, Object data, HttpStatus status) {
        return new ResponseEntity<Response>(new Response(message, data,status, status.value()), status);
    }

    private boolean verifyToken(String jwtToken) {
        APILogger log = new APILogger("verifyToken", UUID.randomUUID().toString());
        log.add("verifyToken-startTime"+ String.valueOf(LocalDateTime.now()),"");
        JWTClaimsSet claims = null;
        try {
            SignedJWT jwtVerify = jwtService.verifySignature(jwtToken);
            if (jwtVerify != null) {
                if (jwtVerify.getJWTClaimsSet() != null) {
                    claims = jwtVerify.getJWTClaimsSet();
                    // Checking Issuer, Audience and expiration
                    if (claims == null) {
                        throw new RuntimeException("Invalid Claims");
                    } else if (!claims.getIssuer().equals(issuer)) {
                        throw new RuntimeException("Invalid Issuer");
                    } else if (!claims.getAudience().get(0).equals(audience)) {
                        throw new RuntimeException("Invalid Audience");
                    } else if (new Date().after(claims.getExpirationTime())) {
                        throw new RuntimeException("Token has expired");
                    }
                } else {
                    log.add("verifyToken-endTime", String.valueOf(LocalDateTime.now()));
                    throw new RuntimeException("Token is Invalid");
                }
            } else {
                return false;
            }

        } catch (ParseException e) {
            log.add("verifyToken-endTime", String.valueOf(LocalDateTime.now()));
            throw new RuntimeException("Unable to Parse the Token");
        }
        log.add("verifyToken-endTime", String.valueOf(LocalDateTime.now()));
        return true;
    }

    private String extractSub(String jwtToken) {
        APILogger log = new APILogger("extractSub", UUID.randomUUID().toString());
        log.add("extractSub-startTime", String.valueOf(LocalDateTime.now()));

        SignedJWT jwtVerify = jwtService.verifySignature(jwtToken);
        JWTClaimsSet claims = null;
        String custId = null;
        try {
            claims = jwtVerify.getJWTClaimsSet();
            if (claims != null) {
                custId = claims.getSubject();
                log.add("extractSub: custId extracted from Token " + custId, "");
            }
        } catch (ParseException e) {
            log.add("extractSub-endTime", String.valueOf(LocalDateTime.now()));
            throw new RuntimeException(e);
        }
        log.add("extractSub-endTime", String.valueOf(LocalDateTime.now()));
        return custId;
    }

    private List<String> extractRoles(String jwtToken) {
        APILogger log = new APILogger("extractSub", UUID.randomUUID().toString());
        log.add("extractSub-startTime", String.valueOf(LocalDateTime.now()));

        SignedJWT jwtVerify = jwtService.verifySignature(jwtToken);
        JWTClaimsSet claims = null;
        List<String> roles = null;
        try {
            claims = jwtVerify.getJWTClaimsSet();
            if (claims != null) {
                roles = (List<String>) claims.getClaim("roles");
                log.add("extractRoles: extracted from Token " + roles, "");
            }
        } catch (ParseException e) {
            log.add("extractSub-endTime", String.valueOf(LocalDateTime.now()));
            throw new RuntimeException(e);
        }
        log.add("extractSub-endTime", String.valueOf(LocalDateTime.now()));
        return roles;
    }

}
