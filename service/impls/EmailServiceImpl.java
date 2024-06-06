package com.bridge.herofincorp.service.impls;
import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.model.dto.EmailDetailsDTO;
import com.bridge.herofincorp.service.EmailService;
import com.sun.mail.smtp.SMTPAddressFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private final JavaMailSender javaMailSender;
    public EmailServiceImpl(JavaMailSender javaMailSender) {
    	this.javaMailSender=javaMailSender;
    }
    

    @Value("${spring.mail.username}")
    private String sender;

    
    public void sendMail(EmailDetailsDTO emailDetailsDTO, APILogger logger)
    {

        logger.add("Sending email to", emailDetailsDTO.getRecipient());
        MimeMessage mimeMessage;
        mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(emailDetailsDTO.getRecipient());
            mimeMessageHelper.setText(emailDetailsDTO.getMessageBody());
            mimeMessageHelper.setSubject(emailDetailsDTO.getSubject());

            if(emailDetailsDTO.getCcRecipient()!=null  && emailDetailsDTO.getCcRecipient().length>1)
                mimeMessageHelper.setCc(emailDetailsDTO.getCcRecipient());

            if(emailDetailsDTO.getFileName()!=null) {
                File file = new File(emailDetailsDTO.getFileName());
                mimeMessageHelper.addAttachment(emailDetailsDTO.getFileName(), file);
            }

            javaMailSender.send(mimeMessage);
            logger.add("Mail sent", "successfully To "+emailDetailsDTO.getRecipient());

        }
        catch (SMTPAddressFailedException ex){
            logger.add("The following email address is invalid:", emailDetailsDTO.getRecipient());
            logger.logError(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), ex);

        }
        catch (Exception e) {
            logger.add("Some error occurred", "in sending email");
            logger.logError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }



    }


}