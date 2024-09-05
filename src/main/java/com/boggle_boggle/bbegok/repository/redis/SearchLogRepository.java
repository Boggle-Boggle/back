package com.boggle_boggle.bbegok.repository.redis;

import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.response.SearchBookListResponse;
import com.boggle_boggle.bbegok.dto.response.SearchLogListResponse;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.redis.SearchLogRedis;
import com.boggle_boggle.bbegok.utils.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchLogRepository {
    @Value("${redis.size}")
    private long RECENT_KEYWORD_SIZE;
    @Value("${redis.prefix}")
    private String KEY_PREFIX;

    private final RedisTemplate<String, SearchLogRedis> redisTemplate;

    public void saveRecentSearchLog(String userId, String keyword){
        String createdAt = LocalDateTimeUtil.getCurrentTimeAsString();
        SearchLogRedis searchLog = new SearchLogRedis(keyword,createdAt);

        String key = KEY_PREFIX + userId;
        if ((redisTemplate.opsForList().size(key) != null) &&
                (redisTemplate.opsForList().size(key) == RECENT_KEYWORD_SIZE)) {
            redisTemplate.opsForList().rightPop(key);
        }
        redisTemplate.opsForList().leftPush(key, searchLog);
    }

    public List<SearchLogRedis> getRecentSearchLogs(String userId) {
        String key = KEY_PREFIX + userId;
        return redisTemplate.opsForList().
                range(key, 0, RECENT_KEYWORD_SIZE);
    }

    public void deleteRecentSearchLog(String userId, String keyword, String createdAt) {
        String key = KEY_PREFIX + userId;
        SearchLogRedis searchLog = new SearchLogRedis(keyword,createdAt);

        long count = redisTemplate.opsForList().remove(key, 1, searchLog);

        if (count == 0) {
            throw new GeneralException(Code.SEARCH_LOG_NOT_EXIST);
        }
    }
}
