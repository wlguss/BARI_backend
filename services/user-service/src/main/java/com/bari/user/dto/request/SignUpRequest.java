package com.bari.user.dto.request;

import com.bari.user.entity.UserRole;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 DTO.
 *
 * 예시 요청 JSON:
 * {
 *   "email": "user@example.com",
 *   "password": "password123",
 *   "nickname": "홍길동",
 *   "role": "USER"    <- 생략 시 기본값 USER
 * }
 */
@Getter
@NoArgsConstructor
public class SignUpRequest {

    /** 이메일 (이메일 형식 검증) */
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    /** 비밀번호 (최소 8자) */
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    /** 비밀번호 확인 */
    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordConfirm;

    /** 닉네임 (2~10자) */
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하이어야 합니다.")
    private String nickname;

    /**
     * 사용자 권한 (기본값: USER).
     * 관리자가 ADMIN/OWNER 계정을 생성할 때 지정합니다.
     */
    private UserRole role = UserRole.USER;

    /** 비밀번호 일치 여부 검증 */
    @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    public boolean isPasswordMatched() {
        return password != null && password.equals(passwordConfirm);
    }
}
