package com.bridge.herofincorp.service;

import com.bridge.herofincorp.model.dto.AclOtpDTO;
import com.bridge.herofincorp.model.dto.OTPResponseDTO;
import org.springframework.http.ResponseEntity;

public interface OtpService {
    String sendOTPSMS(Long phoneNumber, String app) throws Exception;
    String verifyOtp(String phoneNumber, String otp, String app);
    ResponseEntity<OTPResponseDTO> sendMessage(AclOtpDTO dto);
    AclOtpDTO createPayload(Long phoneNumber, String msg ) throws Exception;
}
