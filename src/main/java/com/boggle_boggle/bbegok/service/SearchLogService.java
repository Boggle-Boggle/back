package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.response.SearchLogListResponse;
import com.boggle_boggle.bbegok.repository.redis.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchLogService {
    private final SearchLogRepository searchLogRepository;

    public SearchLogListResponse getRecentSearchLogs(String userId) {
        return SearchLogListResponse.from(searchLogRepository.getRecentSearchLogs(userId));
    }

    public void saveRecentSearchLogs(String userId, String keyword) {
        searchLogRepository.saveRecentSearchLog(userId, keyword);
    }

    public void deleteRecentSearchLog(String userId, String keyword, String createdAt) {
        searchLogRepository.deleteRecentSearchLog(userId, keyword, createdAt);
    }
}
