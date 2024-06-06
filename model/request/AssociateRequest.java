package com.bridge.herofincorp.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class AssociateRequest {
    private String phone;
    private String name;
    private String lastName;
    private String email;
    private List<String> role;
}
