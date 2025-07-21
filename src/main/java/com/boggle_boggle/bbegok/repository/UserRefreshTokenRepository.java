package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.entity.user.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
    UserRefreshToken findByUser_UserId(String userId);
    UserRefreshToken findByUser_UserIdAndRefreshToken(String userId, String refreshToken);

    void deleteByUser(User user);

    Optional<UserRefreshToken> findByRefreshToken(String refreshToken);

    Optional<UserRefreshToken> findByUser_UserIdAndDeviceId(String id, String deviceId);

    void deleteByUser_UserIdAndDeviceId(String userId, String deviceId);

    void deleteByDeviceId(String deviceId);
}

