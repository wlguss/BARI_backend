package com.bari.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증 컴포넌트.
 * jjwt 0.12.x API를 사용합니다.
 *
 * Access Token: userId, role claim 포함 (기본 1시간)
 * Refresh Token: userId만 포함 (기본 7일)
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKeyString;

    /**
     * Access Token 만료 시간 (밀리초, 기본: 1시간)
     */
    @Value("${jwt.expiration:3600000}")
    private long accessTokenExpiration;

    /**
     * Refresh Token 만료 시간 (밀리초, 기본: 7일)
     */
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration;

    private SecretKey secretKey;

    /**
     * 빈 초기화 후 secretKey 생성.
     * secretKeyString을 HMAC-SHA 키로 변환합니다.
     */
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Access Token 생성.
     * claims에 userId(subject), role 포함.
     *
     * @param userId 사용자 ID
     * @param role   사용자 권한 (예: "USER", "ADMIN", "OWNER")
     * @return JWT Access Token 문자열
     */
    public String generateAccessToken(Long userId, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);

        // jjwt 0.12.x API 사용
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성.
     * claims에 userId(subject)만 포함.
     *
     * @param userId 사용자 ID
     * @return JWT Refresh Token 문자열
     */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpiration);

        // jjwt 0.12.x API 사용
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰에서 userId 추출.
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 토큰에서 role 추출.
     *
     * @param token JWT 토큰
     * @return 사용자 권한 문자열
     */
    public String getRole(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * 토큰 유효성 검증.
     *
     * @param token JWT 토큰
     * @return 유효하면 true, 만료/변조 등이면 false
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            log.warn("유효하지 않은 JWT 토큰: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 비어있습니다: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰을 파싱해서 Claims 반환 (내부 헬퍼).
     * jjwt 0.12.x: Jwts.parser().verifyWith().build().parseSignedClaims()
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
