package com.bari.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 DTO.
 *
 * 예시 요청 JSON:
 * {
 *   "email": "user@example.com",
 *   "password": "password123"
 * }
 */
@Getter
@NoArgsConstructor
public class LoginRequest {

    /** 이메일 */
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    /** 비밀번호 */
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
