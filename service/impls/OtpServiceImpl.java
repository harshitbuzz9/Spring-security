package com.bridge.herofincorp.service.impls;

import com.bridge.herofincorp.configs.ClusterConfigurationProperties;
import com.bridge.herofincorp.configs.SmsConfig;
import com.bridge.herofincorp.exceptions.OtpException;
import com.bridge.herofincorp.model.dto.AclOtpDTO;
import com.bridge.herofincorp.model.dto.OTPResponseDTO;
import com.bridge.herofincorp.service.OtpService;
import com.bridge.herofincorp.utils.LoginConstant;
import com.bridge.herofincorp.utils.Security;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class OtpServiceImpl implements OtpService {
    private static final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);
    private final SmsConfig smsConfig;
    private final RedisTemplate redisTemplate;
    private final ClusterConfigurationProperties clusterConfigurationProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public OtpServiceImpl(SmsConfig smsConfig, RedisTemplate redisTemplate, ClusterConfigurationProperties clusterConfigurationProperties) {
        this.smsConfig = smsConfig;
        this.redisTemplate = redisTemplate;
        this.clusterConfigurationProperties = clusterConfigurationProperties;
    }

    @Override
    public String sendOTPSMS(Long phoneNumber, String app) throws Exception {
        System.out.println("app: "+app);
        long otp = Security.generateOtp();
        System.out.println("otp: "+otp);
        String msg = "Dear Hero fincorp Customer your OTP is " + otp + " for requested transaction.";
        AclOtpDTO dto = this.createPayload(phoneNumber, msg );
        System.out.println("AppId: "+dto.getAppid()+" msgId: "+dto.getMsgid()+" Nounce: "+dto.getNounce()+" Encrypted Data: "+dto.getEncryptedData());
        log.info("sendOTPSMS-startTime {}", LocalDateTime.now());
        long currentTime = System.currentTimeMillis();
        savetoRedis(phoneNumber, otp, app);
        ResponseEntity<OTPResponseDTO> response = this.sendMessage(dto);
        log.info("sendOTPSMS-endTime {}", LocalDateTime.now());
        log.info("sendOTPSMS-Total-Time {}", System.currentTimeMillis() - currentTime);
        log.info("setting into Redis as  = {}",otp);
        System.out.println(response.getStatusCode()+", "
                +response.getBody()+", "+
                response.getBody().isAccepted());
        if (response.getStatusCode() == HttpStatus.OK
                && response.getBody() != null
                && response.getBody().isAccepted()) {
            return String.valueOf(otp);
        } else {
            log.info("OTP Service Exception {}", response);
            throw new OtpException("Unable to send OTP..!!"+ response);
        }
    }

    @Override
    public String verifyOtp(String phoneNumber, String otp, String app) {
        log.info("verifyOTP-startTime {} ", LocalDateTime.now());
        String redisKey = LoginConstant.OTP_KEY_PREFIX+ String.valueOf(otp).toString();
        String redisKeyRIndex = String.join("-", LoginConstant.OTP_KEY_PREFIX_REATTEMPT, String.valueOf(phoneNumber).toString() , app);
        System.out.println("redisKeyRIndex: "+redisKeyRIndex);
        String redisKeyTIndex = String.join("-", LoginConstant.OTP_KEY_PREFIX_TIME, String.valueOf(phoneNumber).toString() , app);
        System.out.println("redisKeyTIndex: "+redisKeyTIndex);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        String returnedPhone = null;
        Object oIndex = redisTemplate.opsForValue().get(redisKey);
        System.out.println("oIndex: "+oIndex);
        Object rIndex = redisTemplate.opsForValue().get(redisKeyRIndex);
        System.out.println("rIndex: "+rIndex);
        Object tIndex = rIndex ;
        if (oIndex == null) { // if otp is not found then either reduce the reattmpt or deny based reattempt is exhausted
            return chageReAttementIndex(redisKeyRIndex, returnedPhone, rIndex);
        } else if(oIndex !=null && tIndex ==null) { // if otp is found then either reduce the reattmpt or deny based reattempt is exhausted
            log.info("OTPServiceImpl OTP_INVALID returnedPhone = {}",returnedPhone);
            log.info("verifyOTP-endTime {}", LocalDateTime.now());
            return LoginConstant.OTP_INVALID;
        } else {
            return verifyOtpRetrieved(phoneNumber, redisKey, redisKeyRIndex, redisKeyTIndex, oIndex);
        }
    }

    private String verifyOtpRetrieved(String phoneNumber, String redisKey, String redisKeyRIndex,
                                      String redisKeyTIndex, Object oIndex) {
        String returnedPhone;
        returnedPhone = oIndex.toString();
        System.out.println("OTPServiceImpl.verifyOTP()::returnedPhone " + returnedPhone);
        if (returnedPhone != null && !returnedPhone.equals(phoneNumber)) {
            log.info("OTPServiceImpl OTP_INVALID returnedPhone = {}",returnedPhone.trim());
            log.info("verifyOTP-endTime {}", LocalDateTime.now());
            return LoginConstant.OTP_INVALID;
        } else if (returnedPhone != null && returnedPhone.equals(phoneNumber)) {
            log.info("OTPServiceImpl OTP_VERIFIED returnedPhone = {}",phoneNumber.trim());
            redisTemplate.delete(redisKey);
            redisTemplate.delete(redisKeyTIndex);
            redisTemplate.delete(redisKeyRIndex);
            log.info("verifyOTP-endTime {}", LocalDateTime.now());
            return LoginConstant.OTP_VERIFIED;
        } else {
            log.info("OTPServiceImpl Phone Not Found {}",returnedPhone);
            log.info("OTPServiceImpl Error reading Redis value back");
            log.info("verifyOTP-endTime {}", LocalDateTime.now());
            return LoginConstant.OTP_EXPIRED;
        }
    }

    private String chageReAttementIndex(String redisKeyRIndex, String returnedPhone, Object rIndex) {
        if (rIndex != null) {
            String reattemptCount = rIndex.toString().trim();
            int rCount = Integer.parseInt(reattemptCount);
            if (rCount > 0) {
                redisTemplate.opsForValue().decrement(redisKeyRIndex);
                log.info("OTPServiceImpl OTP_INVALID returnedPhone = {}",returnedPhone);
                log.info("verifyOTP-endTime {}", LocalDateTime.now());
                return LoginConstant.OTP_INVALID;
            }else {
                throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
            }
        } else {
            log.info("OTPServiceImpl OTP_INVALID returnedPhone = {}",returnedPhone);
            log.info("verifyOTP-endTime {}", LocalDateTime.now());
            return LoginConstant.OTP_INVALID;
        }
    }

    public AclOtpDTO createPayload(Long phoneNumber, String msg ) throws Exception {
        String msgId = String.valueOf(System.currentTimeMillis());
        log.info("OTP msg id {}", msgId);
        Map<String, String> otp_sms_body_template = smsConfig.getOtp();
        otp_sms_body_template.put("msgtext",msg);
        otp_sms_body_template.put("msisdn", String.valueOf(phoneNumber));
//        # Encrypt the SMS body template using AES
        String encrypted_data = encryptString(new JSONObject(otp_sms_body_template).toString(),
                smsConfig.getOtpEncryptionKey(),
                smsConfig.getOtpInitVector() );
        System.out.println(encrypted_data);
        return AclOtpDTO.builder()
                .msgid(msgId)
                .appid(smsConfig.getOtpAppId())
                .nounce(smsConfig.getOtpNounce()).encryptedData(encrypted_data).build();
    }

    private String encryptString(String msg, String encryptionKey, String initVector) throws Exception {
        log.info("Encrypting Msg {}", "");
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec secretKeySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

        byte[] encrypted = cipher.doFinal(msg.getBytes());
        return Base64.encodeBase64String(encrypted);
    }

    private void savetoRedis(Long phoneNumber, long otp, String app) throws Exception{
        String redisKeyTIndex = String.join("-", LoginConstant.OTP_KEY_PREFIX_TIME, String.valueOf(phoneNumber).toString() , app);
        System.out.println("redisKeyTIndex: "+redisKeyTIndex);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        if( redisTemplate.opsForValue().get(redisKeyTIndex) != null ) {
            throw new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS);
        }

        String redisKeyRIndex = String.join("-", LoginConstant.OTP_KEY_PREFIX_REATTEMPT, String.valueOf(phoneNumber).toString() , app);
        System.out.println("redisKeyRIndex: "+redisKeyRIndex);
        Duration validity = Duration.ofMinutes(clusterConfigurationProperties.getTimeToLive());
        if (redisTemplate.opsForValue().get(redisKeyRIndex) != null) {
            String reattemptCount = redisTemplate.opsForValue().get(redisKeyRIndex).toString().trim();
            int rCount = Integer.parseInt(reattemptCount);
            if (rCount <= 0) {
                throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
            } else {
                redisTemplate.opsForValue().decrement(redisKeyRIndex);
            }
        } else {
            redisTemplate.opsForValue().set(redisKeyRIndex, LoginConstant.REATTEMPT_COUNT, validity);
        }
        redisTemplate.opsForValue().set(redisKeyTIndex, "true", validity);
        String redisKey = LoginConstant.OTP_KEY_PREFIX+ otp;
        redisTemplate.opsForValue().set(redisKey, String.valueOf(phoneNumber), validity);
    }
    public ResponseEntity<OTPResponseDTO> sendMessage(AclOtpDTO dto){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(smsConfig.getOtpBearerAuth());
        HttpEntity<AclOtpDTO> request = new HttpEntity<>(dto, headers);
        return restTemplate.postForEntity(smsConfig.getOtpUrl(), request, OTPResponseDTO.class);
    }
}