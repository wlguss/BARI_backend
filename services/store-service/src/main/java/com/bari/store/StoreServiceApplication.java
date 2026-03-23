package com.bari.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Store Service 애플리케이션.
 * * [수정 포인트]
 * 1. scanBasePackages를 "com.bari"로 유지하여 User 엔티티 참조 에러 해결
 * 2. 대신 SecurityConfig 파일들에서 @Configuration("이름")을 다르게 주어 충돌 방지
 */
@SpringBootApplication(scanBasePackages = "com.bari")
public class StoreServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreServiceApplication.class, args);
    }
}