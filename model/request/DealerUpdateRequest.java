package com.bridge.herofincorp.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class DealerUpdateRequest {
    private LocalDate birthday;
    private String spouseName;
    private LocalDate marriageAnniversery;
    private LocalDate spouseBirthday;
    private Integer noOfChildren;
}
