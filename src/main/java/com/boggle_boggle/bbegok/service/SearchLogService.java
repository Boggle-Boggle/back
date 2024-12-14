package com.boggle_boggle.bbegok.service;

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

    public List<String> getRecentSearchLogs(String userId) {
        return searchLogRepository.getRecentSearchLogs(userId);
    }

    public void saveRecentSearchLogs(String userId, String keyword) {
        searchLogRepository.saveRecentSearchLog(userId, keyword);
    }

    public void deleteRecentSearchLog(String userId, String keyword) {
        searchLogRepository.deleteRecentSearchLog(userId, keyword);
    }

    public void deleteAllRecentSearchLog(String userId) {
        searchLogRepository.deleteAllRecentSearchLog(userId);
    }
}
