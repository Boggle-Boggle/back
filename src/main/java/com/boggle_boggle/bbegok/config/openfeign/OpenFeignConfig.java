package com.boggle_boggle.bbegok.config.openfeign;

import feign.gson.GsonDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.codec.Decoder;
import feign.Logger;

@Configuration
@EnableFeignClients("com.boggle_boggle.bbegok")
public class OpenFeignConfig {

    @Value("${aladin.ttb.key}")
    private String ttbKey;

    public String getTtbKey() {
        return ttbKey;
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Decoder feignDecoder() {
        return new GsonDecoder();
    }

}
