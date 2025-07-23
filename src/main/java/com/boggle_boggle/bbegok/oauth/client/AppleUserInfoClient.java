package com.boggle_boggle.bbegok.oauth.client;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleUserInfoClient {

    private final ObjectMapper objectMapper;

    public Map<String, Object> getUserInfo(String idToken) {
        String[] parts = idToken.split("\\.");
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

        try {
            return objectMapper.readValue(payloadJson, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Apple ID Token 디코딩 실패", e);
        }
    }
}
