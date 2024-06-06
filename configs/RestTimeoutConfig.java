package com.bridge.herofincorp.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("rest.http")
public class RestTimeoutConfig {
    private int connectTimeout;
    private int readTimeout;
    private int connectRequestTimeout;
}
