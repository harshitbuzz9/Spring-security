package com.bridge.herofincorp.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties("hfcl.sms")
@Data
public class SmsConfig {
        private String otpUrl;
        private String otpAppId;
        private String otpNounce;
        private String otpInitVector;
        private String otpEncryptionKey;
        private String otpBearerAuth;
        private Map<String, String> otp;
        private Map<String, String> customSms;
}
