package com.bridge.herofincorp.model.response;

import com.bridge.herofincorp.model.dto.PanAddressDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PanDetailResponse {
    private String pan;
    private String name;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String dob;
    private Boolean aadhaarLinked;
    private PanAddressDto address;
    private String aadhaarMatch;
    private List profileMatch;
}
