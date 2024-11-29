package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.response.TermsResponse;
import com.boggle_boggle.bbegok.entity.Terms;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.repository.TermsJpaRepository;
import com.boggle_boggle.bbegok.repository.redis.TermsRepository;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final TermsRepository termsRepository;
    private final TermsJpaRepository termsJpaRepository;

    public User getUser(String userId) {
        return userRepository.findByUserId(userId);
    }

    public void updateNicName(String userId, String name) {
        User user = getUser(userId);
        user.updateNickName(name);
    }

    public boolean isNicknameAvailable(String nickname) {
        return userRepository.findByUserName(nickname).isEmpty();
    }

    public void agreeToTerms(String userId) {
        //약관동의 및 권한 업데이트
        User user = getUser(userId);
        user.agreeToTerms();
    }

    //최신 약관 조회
    public TermsResponse getLatestTerms() {
        String latestVersion = termsRepository.getLatestTermsVersion();
        List<Terms> terms = termsJpaRepository.findByVersion(latestVersion);
        return TermsResponse.from(latestVersion, terms);
    }
}
