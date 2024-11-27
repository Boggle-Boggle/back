package com.boggle_boggle.bbegok.repository.user;

import com.boggle_boggle.bbegok.entity.user.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
    UserRefreshToken findByUserId(String userId);
    UserRefreshToken findByUserIdAndRefreshToken(String userId, String refreshToken);

    @Modifying
    @Query("DELETE FROM UserRefreshToken u WHERE u.userId = :userId")
    void deleteRefreshTokenByUserId(@Param("userId") String userId);

    Optional<UserRefreshToken> findByRefreshToken(String refreshToken);
}

