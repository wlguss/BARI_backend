package com.bari.order.config;

import com.bari.security.annotation.CurrentUserIdArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WebMVC 설정.
 * @CurrentUserId 어노테이션이 컨트롤러 파라미터에서 동작하도록 ArgumentResolver를 등록합니다.
 * CORS는 api-gateway의 globalcors에서 일괄 처리합니다.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserIdArgumentResolver());
    }
}
