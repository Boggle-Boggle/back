package com.boggle_boggle.bbegok.config;

import com.boggle_boggle.bbegok.interceptor.AgreementVersionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AgreementVersionInterceptor agreementVersionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /* auth를 제외한 모든 요청에 대해 약관동의여부를 검사함
        registry.addInterceptor(agreementVersionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/**", "/user/**");*/
    }
}
