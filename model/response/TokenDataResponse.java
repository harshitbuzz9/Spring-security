package com.bridge.herofincorp.model.response;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TokenDataResponse {
    private String access_token;
    private int expires_in;
    private String token_type;
}
