package com.bridge.herofincorp.model.response;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor@Builder
public class AssociateResponse {
    private Integer staffId;
    private String dealerCode;
    private String phone;
    private String email;
    private String status;
    private List<String> role;
    private String name;
    private String lastName;
    private String product;
}
