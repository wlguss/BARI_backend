package com.bari.user.dto.response;

import com.bari.user.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 응답 DTO.
 * OWNER 역할인 경우 storeId, storeName 추가 포함.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    /** JWT Access Token (1시간 유효) */
    private final String accessToken;

    /** JWT Refresh Token (7일 유효, Redis에 저장됨) */
    private final String refreshToken;

    /** 사용자 ID */
    private final Long userId;

    /** 사용자 권한 */
    private final UserRole role;

    /** 매장 ID — OWNER 역할인 경우에만 포함 */
    private final Long storeId;

    /** 매장 이름 — OWNER 역할인 경우에만 포함 */
    private final String storeName;

    /** USER / ADMIN 로그인용 */
    public static LoginResponse of(String accessToken, String refreshToken, Long userId, UserRole role) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userId)
                .role(role)
                .build();
    }

    /** OWNER 로그인용 — storeId, storeName 포함 */
    public static LoginResponse ofOwner(String accessToken, String refreshToken, Long userId, UserRole role,
                                        Long storeId, String storeName) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userId)
                .role(role)
                .storeId(storeId)
                .storeName(storeName)
                .build();
    }
}
