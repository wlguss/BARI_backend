package com.bari.security.annotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @CurrentUserId 어노테이션을 처리하는 ArgumentResolver.
 *
 * SecurityContextHolder에서 Authentication을 꺼내
 * principal(userId 문자열)을 Long으로 변환해서 파라미터에 주입합니다.
 *
 * WebMvcConfig에서 등록 필요:
 * {@code
 * @Override
 * public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
 *     resolvers.add(new CurrentUserIdArgumentResolver());
 * }
 * }
 */
@Slf4j
@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 이 ArgumentResolver가 처리할 파라미터인지 확인.
     * @CurrentUserId 어노테이션이 붙어있고 타입이 Long인 경우에만 처리합니다.
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class)
                && Long.class.equals(parameter.getParameterType());
    }

    /**
     * SecurityContext에서 userId를 추출해서 반환.
     * JwtAuthenticationFilter 또는 HeaderAuthenticationFilter에서
     * principal에 userId(String)을 설정했으므로 Long으로 파싱합니다.
     */
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("인증되지 않은 사용자가 @CurrentUserId 파라미터에 접근 시도");
            throw new IllegalStateException("인증 정보를 찾을 수 없습니다.");
        }

        try {
            // principal은 JwtAuthenticationFilter/HeaderAuthenticationFilter에서
            // String.valueOf(userId)로 설정됩니다
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            log.error("userId 파싱 실패: {}", authentication.getName());
            throw new IllegalStateException("유효하지 않은 사용자 ID입니다.");
        }
    }
}
