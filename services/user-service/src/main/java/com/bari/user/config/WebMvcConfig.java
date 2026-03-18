package com.bari.user.config;

import com.bari.security.annotation.CurrentUserIdArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * user-service Web MVC 설정.
 * @CurrentUserId 어노테이션 처리를 위한 ArgumentResolver를 등록합니다.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * @CurrentUserId ArgumentResolver 등록.
     * 컨트롤러 메서드 파라미터에서 @CurrentUserId Long userId를 사용할 수 있게 됩니다.
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserIdArgumentResolver());
    }
}
