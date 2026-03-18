package com.bari.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * User Service 애플리케이션.
 *
 * 담당 기능:
 * - 회원가입, 로그인, 로그아웃
 * - JWT Access Token / Refresh Token 발급
 * - 사용자 정보 조회
 *
 * scanBasePackages = "com.bari":
 * libs:common의 GlobalExceptionHandler(@RestControllerAdvice)를
 * 스캔 대상에 포함시키기 위해 설정합니다.
 *
 * 포트: 8081
 */
@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "com.bari")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
