package com.bridge.herofincorp.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "auth")
@Data
public class AuthProperties {
    private Map<String, String> keys;
}
