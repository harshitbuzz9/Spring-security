package com.bridge.herofincorp.model.response;
import lombok.Getter;

import java.util.List;
@Getter
public class MockPanDetailResponse {
    private final String pan = "AEWPA1238A";
    private final String name = "RISHI AGARWAL";
    private final String firstName = "RISHI";
    private final String middleName = "";
    private final String lastName = "AGARWAL";
    private final String gender = "male";
    private final String dob = "1975-10-10";
    private final Boolean aadhaarLinked = true;
    private final MockPanAddressDto address = new MockPanAddressDto();
    private final String aadhaarMatch = null;
    private final List profileMatch = List.of();
}
