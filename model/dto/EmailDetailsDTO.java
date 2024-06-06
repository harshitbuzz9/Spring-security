package com.bridge.herofincorp.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDetailsDTO {

    private String recipient;
    private String messageBody;
    private String subject;
    private String attachment;
    private String fileName;
    private String[] ccRecipient;

}