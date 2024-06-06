package com.bridge.herofincorp.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class JourneyIngestionDetailResponse {
    private Long journeyId;
    private String email;
    private String name;
    private String product;
    private String offerCode="#DH9029";//todo:tobe changed when available in response
    private String sourceChannel;
    private String mobileNumber;
    private String journeyStage;
    private String journeyStatus;
    private String remarks;
    private String journeyDateTime;
    private String allocatedTo;
    private String appointmentSchedule;
    private String modifiedDate;
    private String journeySubStatus;
    private String createdBy;
    private String city;
    private String state;
}
