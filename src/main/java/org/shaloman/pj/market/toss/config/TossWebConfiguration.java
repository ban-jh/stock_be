package org.shaloman.pj.market.toss.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/** 읽기 전용 토스 컨트롤러 전체에 공통 개인 접근 인증을 적용합니다. */
@Configuration
public class TossWebConfiguration implements WebMvcConfigurer {
    private final TossApiAccessInterceptor accessInterceptor;

    public TossWebConfiguration(TossApiAccessInterceptor accessInterceptor) {
        this.accessInterceptor = accessInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor).addPathPatterns("/api/toss/**");
    }

    /** 개발 Vue 서버의 로컬 주소만 읽기 요청에 허용합니다. */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/toss/**")
                .allowedOrigins("http://localhost:38080", "http://127.0.0.1:38080")
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type")
                .maxAge(600);
    }
}
