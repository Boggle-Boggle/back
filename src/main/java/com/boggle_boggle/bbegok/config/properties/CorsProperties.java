package com.boggle_boggle.bbegok.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private String allowedHeaders;
    private Boolean allowCredentials;
    private Long maxAge = 3600L; // 기본 1시간
}
