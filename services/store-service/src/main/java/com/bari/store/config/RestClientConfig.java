package com.bari.store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${services.discount.url}")
    private String discountServiceUrl;

    @Bean
    RestClient discountRestClient() {
        return RestClient.builder()
                .baseUrl(discountServiceUrl)
                .build();
    }
}
