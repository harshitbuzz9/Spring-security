package com.bridge.herofincorp.model.dto;

import lombok.Data;

@Data
public class OTPResponseDTO {
    private String respid;
    private boolean accepted;
}
