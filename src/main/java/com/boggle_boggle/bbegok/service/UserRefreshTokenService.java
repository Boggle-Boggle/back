package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.repository.UserRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRefreshTokenService {
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public void deleteRefreshTokenByUserIdAndDeviceId(String userId, String deviceId) {
        userRefreshTokenRepository.deleteByUser_UserIdAndDeviceId(userId, deviceId);
    }

    public void deleteByDeviceId(String deviceId) {
        userRefreshTokenRepository.deleteByDeviceId(deviceId);
    }
}