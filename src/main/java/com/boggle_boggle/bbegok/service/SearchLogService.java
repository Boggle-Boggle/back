package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.SearchLogs;
import com.boggle_boggle.bbegok.dto.response.SearchLogListResponse;
import com.boggle_boggle.bbegok.repository.redis.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchLogService {
    private final SearchLogRepository searchLogRepository;

    public List<SearchLogs> getRecentSearchLogs(String userId) {
        return SearchLogListResponse.from(searchLogRepository.getRecentSearchLogs(userId)).getRecentSearchLogs();
    }

    public void saveRecentSearchLogs(String userId, String keyword) {
        searchLogRepository.saveRecentSearchLog(userId, keyword);
    }

    public void deleteRecentSearchLog(String userId, String keyword, String createdAt) {
        searchLogRepository.deleteRecentSearchLog(userId, keyword, createdAt);
    }
}
