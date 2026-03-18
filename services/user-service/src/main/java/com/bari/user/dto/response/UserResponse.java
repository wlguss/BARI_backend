package com.bari.user.dto.response;

import com.bari.user.entity.User;
import com.bari.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자 정보 응답 DTO.
 *
 * 응답 예시:
 * {
 *   "id": 1,
 *   "email": "user@example.com",
 *   "nickname": "홍길동",
 *   "role": "USER",
 *   "createdAt": "2024-01-01T00:00:00"
 * }
 */
@Getter
@Builder
public class UserResponse {

    private final Long id;
    private final String email;
    private final String nickname;
    private final UserRole role;
    private final LocalDateTime createdAt;

    /**
     * User 엔티티에서 UserResponse 생성.
     *
     * @param user User 엔티티
     * @return UserResponse
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
