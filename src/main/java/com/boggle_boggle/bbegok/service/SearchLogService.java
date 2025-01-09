package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.redis.SearchLogRepository;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchLogService {
    private final SearchLogRepository searchLogRepository;
    private final UserRepository userRepository;

    public User getUser(String userId) {
        User user = userRepository.findByUserId(userId);
        if(user.getIsDeleted()) throw new GeneralException(Code.USER_ALREADY_WITHDRAWN);
        return user;
    }

    public List<String> getRecentSearchLogs(String userId) {
        return searchLogRepository.getRecentSearchLogs(getUser(userId).getUserSeq());
    }

    public void saveRecentSearchLogs(String userId, String keyword) {
        searchLogRepository.saveRecentSearchLog(getUser(userId).getUserSeq(), keyword);
    }

    public void deleteRecentSearchLog(String userId, String keyword) {
        searchLogRepository.deleteRecentSearchLog(getUser(userId).getUserSeq(), keyword);
    }

    public void deleteAllRecentSearchLog(String userId) {
        searchLogRepository.deleteAllRecentSearchLog(getUser(userId).getUserSeq());
    }
}
