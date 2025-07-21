package com.boggle_boggle.bbegok.oauth.client;

import com.boggle_boggle.bbegok.oauth.client.impl.GoogleOAuth2Client;
import com.boggle_boggle.bbegok.oauth.client.impl.KakaoOAuth2Client;
import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

//인증 service단에서 각 provider별 client를 자동으로 주입받기 위한 클래스
//전략패턴 + EnumMap 기반 빈 주입
@Configuration
public class OAuth2ProviderClientConfig {

    @Bean
    public Map<ProviderType, OAuth2ProviderClient> providerClients(
            KakaoOAuth2Client kakaoClient,
            GoogleOAuth2Client googleClient
    ) {
        Map<ProviderType, OAuth2ProviderClient> map = new EnumMap<>(ProviderType.class);
        map.put(ProviderType.KAKAO, kakaoClient);
        map.put(ProviderType.GOOGLE, googleClient);

        return map;
    }
}