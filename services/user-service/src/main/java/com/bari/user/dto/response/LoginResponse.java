package com.bari.user.dto.response;

import com.bari.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 응답 DTO.
 *
 * 응답 예시:
 * {
 *   "accessToken": "eyJhbGci...",
 *   "refreshToken": "eyJhbGci...",
 *   "userId": 1,
 *   "role": "USER"
 * }
 */
@Getter
@Builder
public class LoginResponse {

    /** JWT Access Token (1시간 유효) */
    private final String accessToken;

    /** JWT Refresh Token (7일 유효, Redis에 저장됨) */
    private final String refreshToken;

    /** 사용자 ID */
    private final Long userId;

    /** 사용자 권한 */
    private final UserRole role;

    /**
     * 정적 팩토리 메서드.
     *
     * @param accessToken  발급된 Access Token
     * @param refreshToken 발급된 Refresh Token
     * @param userId       사용자 ID
     * @param role         사용자 권한
     * @return LoginResponse
     */
    public static LoginResponse of(String accessToken, String refreshToken, Long userId, UserRole role) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userId)
                .role(role)
                .build();
    }
}
