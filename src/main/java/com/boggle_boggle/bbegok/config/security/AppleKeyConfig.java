package com.boggle_boggle.bbegok.config.security;

import com.boggle_boggle.bbegok.config.properties.OAuthProperties;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

@Configuration
@RequiredArgsConstructor
public class AppleKeyConfig {

    private final OAuthProperties oAuthProperties;

    @Bean
    public ECPrivateKey applePrivateKey() throws Exception {
        ClassPathResource resource = new ClassPathResource(oAuthProperties.getApple().getKeyPath()); // classpath:key/AuthKey.p8

        try (PemReader pemReader = new PemReader(new InputStreamReader(resource.getInputStream()))) {
            byte[] content = pemReader.readPemObject().getContent();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);
            KeyFactory kf = KeyFactory.getInstance("EC");
            return (ECPrivateKey) kf.generatePrivate(keySpec);
        }
    }
}
