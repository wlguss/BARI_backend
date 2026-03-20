package com.bari.user.controller;

import com.bari.common.response.ApiResponse;
import com.bari.security.annotation.CurrentUserId;
import com.bari.user.dto.response.UserResponse;
import com.bari.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 정보 API 컨트롤러.
 * 인증이 필요한 사용자 정보 관련 API를 처리합니다.
 */
@Tag(name = "사용자 API", description = "사용자 정보 조회 (인증 필요)")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 내 정보 조회.
     * @CurrentUserId 어노테이션으로 SecurityContext에서 userId를 자동 추출합니다.
     *
     * 요청 예시:
     * GET /api/users/me
     * Authorization: Bearer {accessToken}
     *
     * @param userId 현재 로그인한 사용자 ID (자동 주입)
     * @return 사용자 정보 (200 OK)
     */
    @Operation(
            summary = "내 정보 조회",
            description = "JWT 토큰으로 현재 로그인한 사용자의 정보를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@Parameter(hidden = true) @CurrentUserId Long userId) {
        UserResponse response = userService.getUser(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 사용자를 탈퇴 처리합니다. (soft delete)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(@Parameter(hidden = true) @CurrentUserId Long userId) {
        userService.withdraw(userId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
