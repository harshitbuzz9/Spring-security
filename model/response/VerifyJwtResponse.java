package com.bridge.herofincorp.model.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VerifyJwtResponse {

    private String message;
    private String status;
    public int statusCode;
}
