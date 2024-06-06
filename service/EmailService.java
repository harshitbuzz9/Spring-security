package com.bridge.herofincorp.service;


import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.model.dto.EmailDetailsDTO;

public interface EmailService {
     void sendMail(EmailDetailsDTO emailDetails, APILogger logger);

}