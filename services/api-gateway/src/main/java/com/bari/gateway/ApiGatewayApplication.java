package com.bari.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway 애플리케이션.
 *
 * Spring Cloud Gateway는 WebFlux(Reactive) 기반으로 동작합니다.
 * 모든 마이크로서비스의 단일 진입점 역할을 합니다.
 *
 * 주요 역할:
 * - JWT 검증 (JwtGatewayFilter)
 * - X-User-Id, X-User-Role 헤더 주입
 * - 라우팅 (user-service, item-service 등)
 *
 * 포트: 8080
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
