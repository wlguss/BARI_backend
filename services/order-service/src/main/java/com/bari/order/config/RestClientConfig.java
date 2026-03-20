package com.bari.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * 서비스 간 동기 통신을 위한 RestClient 설정.
 */
@Configuration
public class RestClientConfig {

    @Value("${services.product.url}")
    private String productServiceUrl;

    @Value("${services.store.url}")
    private String storeServiceUrl;

    /**
     * product-service 전용 RestClient.
     */
    @Bean
    public RestClient productRestClient() {
        return RestClient.builder()
                .baseUrl(productServiceUrl)
                .build();
    }

    /**
     * store-service 전용 RestClient.
     */
    @Bean
    public RestClient storeRestClient() {
        return RestClient.builder()
                .baseUrl(storeServiceUrl)
                .build();
    }
}
