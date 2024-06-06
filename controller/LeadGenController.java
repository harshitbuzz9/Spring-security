package com.bridge.herofincorp.controller;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.model.request.LoginRequest;
import com.bridge.herofincorp.model.request.PanRequest;
import com.bridge.herofincorp.model.request.VerifyOtpRequest;
import com.bridge.herofincorp.model.response.OtpVerificationResponse;
import com.bridge.herofincorp.model.response.PanResponse;
import com.bridge.herofincorp.model.response.SendOtpResponse;
import com.bridge.herofincorp.service.LeadGenService;
import com.bridge.herofincorp.service.OtpService;
import com.bridge.herofincorp.utils.LoginConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/bridge-app/leadgen")
public class LeadGenController {
    @Autowired
    private OtpService otpService;
    @Autowired
    private LeadGenService service;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/verify/otp")
    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    public OtpVerificationResponse verifyOtp(@RequestBody VerifyOtpRequest request){
        OtpVerificationResponse response = new OtpVerificationResponse();
        response.setIsOtpVerified(Objects.equals(otpService.verifyOtp(request.getPhone(), request.getOtp(), request.getAppName()), LoginConstant.OTP_VERIFIED));
        return response;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/send/otp")
    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    public SendOtpResponse verifyOtp(@RequestBody LoginRequest request) throws Exception {
        SendOtpResponse response = new SendOtpResponse();
        response.setIsOtpSent(otpService.sendOTPSMS(Long.parseLong(request.getPhone()), request.getAppName())!=null);
        return response;
    }
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/pan")
    @PreAuthorize("hasAnyRole('LEADGENERATION','ADMIN')")
    public PanResponse getPanDetails(@RequestAttribute("logger")APILogger logger, @RequestBody PanRequest request) {
        logger.add("LeadGenController.getPanDetails()-startTime"+ LocalDateTime.now(),"");
        PanResponse response;
        try{
            response = service.getPanDetails(request,logger);
            logger.add("LeadGenController.getPanDetails()-endTime"+ LocalDateTime.now(),"");
            logger.logSuccess(200);
        }catch (Exception e){
            logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
            throw e;
        }
        return response;
    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/pincode/{pinCode}")
    public Object getPinCodeInfo(@RequestAttribute("logger") APILogger logger, @PathVariable String pinCode, @RequestParam String product){
        Object response;
        try{
            logger.add("UclLeadGenController.getPinCodeInfo()-startTime"+ LocalDateTime.now(),"");
            response =  service.getPinCodeInfo(pinCode,product,logger);
            logger.add("UclLeadGenController.getPinCodeInfo()-endTime"+ LocalDateTime.now(),"");
            logger.logSuccess(200);
        }catch (Exception e){
            logger.logError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            throw e;
        }
        return response;
    }
}
