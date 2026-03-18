package com.bari.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메서드 파라미터에 사용하는 어노테이션.
 * SecurityContext에서 현재 로그인한 사용자의 ID를 자동으로 주입합니다.
 *
 * 사용 예시:
 * {@code
 * @GetMapping("/me")
 * public ResponseEntity<UserResponse> getMe(@CurrentUserId Long userId) {
 *     return ResponseEntity.ok(ApiResponse.success(userService.getUser(userId)));
 * }
 * }
 *
 * CurrentUserIdArgumentResolver가 이 어노테이션을 처리합니다.
 * WebMvcConfig에서 ArgumentResolver로 등록해야 동작합니다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUserId {
}
