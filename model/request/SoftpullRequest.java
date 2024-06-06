package com.bridge.herofincorp.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class SoftpullRequest {
    private String firstName;
    private String lastName;
    private String dob;
    private String mobileNumber;
    private String consent;
    private String sourceSystem;
    private String sourceLead;
    private String pin;
    private String leadId;
    private String webhook;
    private String stageIndicator;
}
