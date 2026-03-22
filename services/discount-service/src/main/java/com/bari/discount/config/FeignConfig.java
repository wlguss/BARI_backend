package com.bari.discount.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import feign.RequestInterceptor;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null) {
                String userId = (String) authentication.getPrincipal();

                String role = authentication.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse("ROLE_USER")
                        .replace("ROLE_", "");

                requestTemplate.header("X-User-Id", userId);
                requestTemplate.header("X-User-Role", role);
            }
        };
    }
}