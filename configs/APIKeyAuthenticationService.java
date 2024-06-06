package com.bridge.herofincorp.configs;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class APIKeyAuthenticationService {
    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";

    public static void getAuthentication(HttpServletRequest request, Set<String> keysT) {

        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);

        if (apiKey == null || !keysT.contains(apiKey)) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid API Key");

        }

    }
}
