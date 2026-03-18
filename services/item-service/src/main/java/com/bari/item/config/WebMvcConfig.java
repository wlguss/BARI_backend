package com.bari.item.config;

import com.bari.security.annotation.CurrentUserIdArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * item-service Web MVC 설정.
 *
 * [X-Header 인증 방식의 핵심]
 * @CurrentUserId 어노테이션 처리를 위한 ArgumentResolver를 등록합니다.
 *
 * 동작 방식:
 * 1. HeaderAuthenticationFilter가 X-User-Id 헤더에서 userId를 추출해 SecurityContext에 설정
 * 2. CurrentUserIdArgumentResolver가 SecurityContext에서 userId를 꺼내 파라미터에 주입
 * 3. 컨트롤러 메서드에서 @CurrentUserId Long userId로 편리하게 사용
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * @CurrentUserId ArgumentResolver 등록.
     * 컨트롤러 메서드 파라미터에서 @CurrentUserId Long userId를 사용할 수 있게 됩니다.
     *
     * 사용 예시:
     * {@code
     * @PostMapping
     * public ResponseEntity<?> createItem(@RequestBody ItemRequest request,
     *                                     @CurrentUserId Long userId) {
     *     // userId는 X-User-Id 헤더에서 자동으로 추출됩니다
     * }
     * }
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserIdArgumentResolver());
    }
}
