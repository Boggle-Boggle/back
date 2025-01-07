package com.boggle_boggle.bbegok.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class UserRefreshToken {
    @JsonIgnore
    @Id
    @Column(name = "refresh_token_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenSeq;

    @OneToOne
    @JoinColumn(name = "user_seq")
    private User user;

//    @Column(name = "user_id", length = 64)
//    private String userId;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Column(name = "device_id", length = 50)
    private String deviceId;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    protected UserRefreshToken(){}

    protected UserRefreshToken(
            User user,
            String refreshToken,
            String deviceId) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.deviceId = deviceId;
        this.lastLoginAt = LocalDateTime.now();
    }

    public static UserRefreshToken createUserRefreshToken(
            User user,
            String refreshToken,
            String deviceId){
        return new UserRefreshToken(user, refreshToken, deviceId);
    }

    public void updateRefreshToken(String token) {
        this.refreshToken = token;
        this.lastLoginAt = LocalDateTime.now();
    }

    public String getUserId() {
        return this.user.getUserId();
    }
}
