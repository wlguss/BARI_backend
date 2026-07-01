package com.bari.security.header;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * X-Header 인증 필터 — item-service 등 downstream 서비스에서 사용.
 *
 * [인증 흐름]
 * 클라이언트 → api-gateway (JWT 검증) → X-User-Id, X-User-Role 헤더 주입 → downstream 서비스
 *
 * api-gateway가 JWT를 검증한 후 X-User-Id, X-User-Role 헤더를 추가해서 전달합니다.
 * downstream 서비스는 이 헤더를 신뢰하고 SecurityContext에 인증 정보를 설정합니다.
 *
 * [직접 서비스 호출 시]
 * api-gateway 없이 직접 호출할 때는 헤더를 수동으로 설정해야 합니다:
 * curl -H "X-User-Id: 1" -H "X-User-Role: USER" http://localhost:8087/api/items
 *
 * 처리 흐름:
 * 1. X-User-Id 헤더에서 userId 추출
 * 2. X-User-Role 헤더에서 role 추출 (없으면 기본값 "USER")
 * 3. SecurityContext에 Authentication 설정
 */
@Slf4j
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    /** userId가 전달되는 헤더명 */
    private static final String USER_ID_HEADER = "X-User-Id";

    /** role이 전달되는 헤더명 */
    private static final String USER_ROLE_HEADER = "X-User-Role";

    /** role 헤더가 없을 때 사용할 기본값 */
    private static final String DEFAULT_ROLE = "USER";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. X-User-Id 헤더에서 userId 추출
        String userIdHeader = request.getHeader(USER_ID_HEADER);

        if (StringUtils.hasText(userIdHeader)) {
            try {
                Long userId = Long.parseLong(userIdHeader);

                // 2. X-User-Role 헤더에서 role 추출 (없으면 기본값 "USER")
                String role = request.getHeader(USER_ROLE_HEADER);
                if (!StringUtils.hasText(role)) {
                    role = DEFAULT_ROLE;
                }

                // Spring Security 권한명 규칙: "ROLE_" 접두사 필요
                String authority = "ROLE_" + role;

                // 3. SecurityContext에 Authentication 설정
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                String.valueOf(userId),   // principal: userId (String)
                                null,                     // credentials: null (헤더 기반이므로)
                                List.of(new SimpleGrantedAuthority(authority))
                        );
                
                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("X-Header 인증 성공 - userId: {}, role: {}", userId, role);

            } catch (NumberFormatException e) {
                log.warn("X-User-Id 헤더 값이 올바르지 않습니다: {}", userIdHeader);
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
