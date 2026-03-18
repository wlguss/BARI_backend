package com.bari.item.config;

import com.bari.security.header.HeaderAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * item-service Security 설정.
 *
 * [인증 방식: X-Header]
 * api-gateway에서 JWT 검증 후 X-User-Id, X-User-Role 헤더가 주입됩니다.
 * HeaderAuthenticationFilter가 이 헤더를 읽어 SecurityContext를 설정합니다.
 *
 * [접근 권한]
 * - GET /api/items, GET /api/items/** : 인증 없이 접근 가능 (공개 조회)
 * - POST, PUT, DELETE /api/items/** : 인증 필요
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Security 필터 체인 설정.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (REST API + 헤더 기반 인증)
                .csrf(AbstractHttpConfigurer::disable)

                // Form 로그인, HTTP Basic 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션 비활성화 (X-Header 기반 STATELESS)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 아이템 조회는 인증 없이 접근 가능 (공개)
                        .requestMatchers(HttpMethod.GET, "/api/items", "/api/items/**").permitAll()
                        // Swagger UI는 누구나 접근 가능
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs").permitAll()
                        // Actuator 헬스체크는 누구나 접근 가능
                        .requestMatchers("/actuator/**").permitAll()
                        // 나머지 (POST, PUT, DELETE)는 인증 필요
                        .anyRequest().authenticated()
                )

                // X-Header 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
                // api-gateway가 주입한 X-User-Id, X-User-Role 헤더를 처리합니다
                .addFilterBefore(
                        new HeaderAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
