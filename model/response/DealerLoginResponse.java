package com.bridge.herofincorp.model.response;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DealerLoginResponse {
    public String dealer_code;
    public String status;
}
