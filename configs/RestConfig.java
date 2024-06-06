package com.bridge.herofincorp.configs;

import com.bridge.herofincorp.exceptions.ApplicationExceptionHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {
    private final RestTimeoutConfig restTimeoutConfig;

    public RestConfig(RestTimeoutConfig restTimeoutConfig) {
        this.restTimeoutConfig = restTimeoutConfig;
    }

    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.errorHandler(new ApplicationExceptionHandler()).build();
        setConnectionTimeout(restTemplate);
        return restTemplate;
    }

    private void setConnectionTimeout(RestTemplate restTemplate) {
        ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();
        if (requestFactory instanceof HttpComponentsClientHttpRequestFactory) {
            HttpComponentsClientHttpRequestFactory httpRequestFactory = (HttpComponentsClientHttpRequestFactory) requestFactory;
            httpRequestFactory.setConnectionRequestTimeout(restTimeoutConfig.getConnectRequestTimeout());
            httpRequestFactory.setConnectTimeout(restTimeoutConfig.getConnectTimeout());
            //httpRequestFactory.setReadTimeout(restTimeoutConfig.getReadTimeout());
        }
    }
}
