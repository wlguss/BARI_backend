package com.bari.user.controller;

import com.bari.common.exception.BusinessException;
import com.bari.common.response.ApiResponse;
import com.bari.security.annotation.CurrentUserId;
import com.bari.user.dto.request.LoginRequest;
import com.bari.user.dto.request.SignUpRequest;
import com.bari.user.dto.response.LoginResponse;
import com.bari.user.dto.response.UserResponse;
import com.bari.user.exception.UserErrorCode;
import com.bari.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API 컨트롤러.
 * 회원가입, 로그인, 토큰 갱신, 로그아웃을 처리합니다.
 */
@Tag(name = "인증 API", description = "회원가입, 로그인, 토큰 갱신, 로그아웃")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 회원가입.
     * 이메일 중복 체크 후 새 사용자를 등록합니다.
     *
     * @param request 회원가입 요청 DTO
     * @return 생성된 사용자 정보 (201 Created)
     */
    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임으로 새 계정을 생성합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        UserResponse response = userService.signUp(request);
        return ResponseEntity.status(201).body(ApiResponse.created(response));
    }

    /**
     * 로그인.
     * 이메일/비밀번호 검증 후 JWT 토큰을 발급합니다.
     *
     * @param request 로그인 요청 DTO
     * @return Access Token, Refresh Token (200 OK)
     */
    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 토큰 갱신.
     * Authorization 헤더의 Refresh Token으로 새 Access Token을 발급합니다.
     *
     * 요청 예시:
     * Authorization: Bearer {refreshToken}
     *
     * @param authorization Authorization 헤더 값
     * @return 새 Access Token, Refresh Token (200 OK)
     */
    @Operation(
            summary = "토큰 갱신",
            description = "Refresh Token으로 새 Access Token을 발급받습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authorization) {

        // "Bearer " 접두사 제거
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(UserErrorCode.INVALID_TOKEN);
        }

        String refreshToken = authorization.substring(7);
        LoginResponse response = userService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 로그아웃.
     * Redis에서 Refresh Token을 삭제합니다.
     * @CurrentUserId 어노테이션으로 SecurityContext에서 userId를 자동 추출합니다.
     *
     * @param userId 현재 로그인한 사용자 ID (자동 주입)
     * @return 200 OK
     */
    @Operation(
            summary = "로그아웃",
            description = "Redis에서 Refresh Token을 삭제하여 로그아웃합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Parameter(hidden = true) @CurrentUserId Long userId) {
        userService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
