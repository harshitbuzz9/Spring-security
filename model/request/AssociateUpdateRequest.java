package com.bridge.herofincorp.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class AssociateUpdateRequest {
    private String name;
    private String lastName;
    private String email;
    private String status;
    private List<String> role;
}
