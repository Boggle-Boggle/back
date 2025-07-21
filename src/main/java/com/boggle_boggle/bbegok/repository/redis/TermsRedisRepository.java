package com.boggle_boggle.bbegok.repository.redis;

import com.boggle_boggle.bbegok.redis.SearchLogRedis;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TermsRedisRepository {
    @Value("${redis.terms.key}")
    private String TERMS_VERSION_KEY;

    private final StringRedisTemplate redisTemplate;

    // 최신 약관 버전 조회
    public String getLatestTermsVersion() {
        return redisTemplate.opsForValue().get(TERMS_VERSION_KEY);
    }

}
