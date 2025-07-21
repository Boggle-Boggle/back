package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.redis.SearchLogRepository;
import com.boggle_boggle.bbegok.repository.UserRepository;
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

    public User getUser(String userSeq) {
        User user = userRepository.findByUserSeqAndIsDeleted(Long.valueOf(userSeq), false);
        if(user == null) {
            //탈퇴한 적 있는 회원
            if(userRepository.countByUserSeqAndIsDeleted(Long.valueOf(userSeq), true) > 0) throw new GeneralException(Code.USER_ALREADY_WITHDRAWN);
            else throw new GeneralException(Code.USER_NOT_FOUND);
        }
        return user;
    }

    public List<String> getRecentSearchLogs(String userSeq) {
        return searchLogRepository.getRecentSearchLogs(getUser(userSeq).getUserSeq());
    }

    public void saveRecentSearchLogs(String userSeq, String keyword) {
        searchLogRepository.saveRecentSearchLog(getUser(userSeq).getUserSeq(), keyword);
    }

    public void deleteRecentSearchLog(String userSeq, String keyword) {
        searchLogRepository.deleteRecentSearchLog(getUser(userSeq).getUserSeq(), keyword);
    }

    public void deleteAllRecentSearchLog(String userSeq) {
        searchLogRepository.deleteAllRecentSearchLog(getUser(userSeq).getUserSeq());
    }
}
