package com.bridge.herofincorp.model.request;

import com.bridge.herofincorp.model.dto.JourneyTwlDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TwlRequest {
    private String product;
    private String sourceChannel;
    private String subChannel;
    private String mobileNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String pan;
    private String currentResiAddLine1;
    private String currentResiAddLine2;
    private String currentResiAddLine3;
    private String currentResidentCity;
    private String currentResidentState;
    private Long currentResidentPin;
    private JourneyTwlDTO journeyTwlDTO;
}
