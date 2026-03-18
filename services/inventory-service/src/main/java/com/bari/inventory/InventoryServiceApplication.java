package com.bari.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Inventory Service 애플리케이션.
 *
 * [인증 방식: X-Header]
 * api-gateway에서 JWT 검증 후 X-User-Id, X-User-Role 헤더를 주입합니다.
 * 이 서비스는 헤더를 신뢰하고 SecurityContext에 인증 정보를 설정합니다.
 *
 * scanBasePackages = "com.bari":
 * libs:common의 GlobalExceptionHandler를 스캔하기 위해 설정합니다.
 *
 * 포트: 8084
 */
@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "com.bari")
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
