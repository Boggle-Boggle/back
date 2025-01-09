package com.boggle_boggle.bbegok.repository.redis;

import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.redis.SearchLogRedis;
import com.boggle_boggle.bbegok.utils.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SearchLogRepository {
    @Value("${redis.size}")
    private long RECENT_KEYWORD_SIZE;
    @Value("${redis.prefix}")
    private String KEY_PREFIX;

    private final RedisTemplate<String, String> redisTemplate;

    public void saveRecentSearchLog(Long userSeq, String keyword) {
        String key = KEY_PREFIX + userSeq;

        // 현재 시간을 점수로 설정
        long score = System.currentTimeMillis();

        // 검색어 추가 (중복 시 덮어쓰기)
        redisTemplate.opsForZSet().add(key, keyword, score);

        // 최대 크기 초과 시 오래된 항목 제거
        Long size = redisTemplate.opsForZSet().size(key);
        if (size != null && size > RECENT_KEYWORD_SIZE) {
            redisTemplate.opsForZSet().removeRange(key, 0, size - RECENT_KEYWORD_SIZE - 1);
        }
    }

    public List<String> getRecentSearchLogs(Long userId) {
        String key = KEY_PREFIX + userId;

        // 최근 검색어를 점수 기준으로 내림차순 조회
        Set<String> resultSet = redisTemplate.opsForZSet()
                .reverseRange(key, 0, RECENT_KEYWORD_SIZE - 1);

        // Set을 List로 변환 후 반환
        return resultSet != null ? new ArrayList<>(resultSet) : Collections.emptyList();
    }

    public void deleteRecentSearchLog(Long userId, String keyword) {
        String key = KEY_PREFIX + userId;

        // 검색어 삭제
        Long removed = redisTemplate.opsForZSet().remove(key, keyword);

        // 삭제된 항목이 없으면 예외 발생
        if (removed == null || removed == 0) {
            throw new GeneralException(Code.SEARCH_LOG_NOT_EXIST);
        }
    }

    public void deleteAllRecentSearchLog(Long userId) {
        String key = KEY_PREFIX + userId;

        // 리스트 존재 여부 확인
        Long size = redisTemplate.opsForZSet().size(key);
        if (size == null || size == 0) {
            throw new GeneralException(Code.SEARCH_LOG_NOT_EXIST);
        }

        // 키 삭제
        redisTemplate.delete(key);
    }


}
