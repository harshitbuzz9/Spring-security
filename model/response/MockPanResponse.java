package com.bridge.herofincorp.model.response;

import lombok.Getter;

@Getter
public class MockPanResponse {
    private final String requestId = "1802f4d0-93a8-4c20-bb36-e7ade17c7296";
    private final MockPanDetailResponse result = new MockPanDetailResponse();
    private final Integer statusCode = 101;
    private final String applicationId = "999999999999";
}