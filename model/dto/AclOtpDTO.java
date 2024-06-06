package com.bridge.herofincorp.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AclOtpDTO {
    private String appid;
    private String nounce;
    private String encryptedData;
    private String msgid;
}
