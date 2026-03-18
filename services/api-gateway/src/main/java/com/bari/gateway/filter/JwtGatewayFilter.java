package com.bari.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT Gateway 필터 — api-gateway의 핵심 필터.
 *
 * [처리 흐름]
 * 1. Public Path 여부 확인 (로그인, 회원가입 등은 스킵)
 * 2. Authorization 헤더에서 JWT 추출
 * 3. JWT 유효성 검증
 * 4. 검증 성공 시 X-User-Id, X-User-Role 헤더를 추가하여 downstream으로 전달
 * 5. 검증 실패 시 401 Unauthorized 반환
 *
 * [Spring Cloud Gateway의 Reactive 방식]
 * WebFlux 기반이므로 Mono/Flux를 사용합니다.
 * ServerWebExchange로 요청/응답을 처리합니다.
 */
@Slf4j
@Component
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secretKeyString;

    private SecretKey secretKey;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * JWT 검증 없이 통과시킬 Public Path 목록.
     * 로그인, 회원가입, 헬스체크 등
     */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/signup",
            "/actuator/**",
            "/actuator/health/**"
    );

    /**
     * 조회 요청은 인증 없이 허용 (GET 메서드 + 특정 경로)
     */
    private static final List<String> PUBLIC_GET_PATHS = List.of(
            "/api/items",
            "/api/items/**"
    );

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        // 1. Public Path 확인 — 토큰 검증 스킵
        if (isPublicPath(path, method)) {
            log.debug("Public path 요청 스킵: {} {}", method, path);
            return chain.filter(exchange);
        }

        // 2. Authorization 헤더에서 JWT 토큰 추출
        String token = resolveToken(request);

        if (!StringUtils.hasText(token)) {
            log.warn("토큰 없음 - 요청 거부: {} {}", method, path);
            return unauthorized(exchange.getResponse());
        }

        // 3. JWT 토큰 검증
        try {
            Claims claims = parseClaims(token);
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            if (role == null) {
                role = "USER";
            }

            log.debug("JWT 검증 성공 - userId: {}, role: {}", userId, role);

            // 4. X-User-Id, X-User-Role 헤더 추가 후 downstream으로 전달
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (JwtException e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            return unauthorized(exchange.getResponse());
        } catch (Exception e) {
            log.error("JWT 처리 중 오류: {}", e.getMessage());
            return unauthorized(exchange.getResponse());
        }
    }

    /**
     * 필터 순서 — 낮을수록 먼저 실행 (최우선 실행)
     */
    @Override
    public int getOrder() {
        return -100;
    }

    /**
     * Public Path 여부 확인.
     * GET 요청의 조회 경로 또는 인증이 필요 없는 경로인지 확인합니다.
     */
    private boolean isPublicPath(String path, String method) {
        // 항상 공개인 경로
        boolean alwaysPublic = PUBLIC_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));

        if (alwaysPublic) {
            return true;
        }

        // GET 메서드 + 공개 경로
        if ("GET".equals(method)) {
            return PUBLIC_GET_PATHS.stream()
                    .anyMatch(pattern -> pathMatcher.match(pattern, path));
        }

        return false;
    }

    /**
     * Authorization 헤더에서 Bearer 토큰 추출.
     */
    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * JWT Claims 파싱.
     * jjwt 0.12.x API 사용
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 401 Unauthorized 응답 반환.
     */
    private Mono<Void> unauthorized(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}
