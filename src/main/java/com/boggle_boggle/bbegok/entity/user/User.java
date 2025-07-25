package com.boggle_boggle.bbegok.entity.user;

import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.oauth.entity.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class User {

    @JsonIgnore
    @Id
    @Column(name = "user_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userSeq;

    @Column(name = "user_id", length = 64)
    @NotNull
    @Size(max = 64)
    private String userId;

    @Column(name = "user_name", unique = true, length = 15)
    private String userName = null;

    @Column(name = "withdraw_user_name", length = 12)
    private String withdrawUserName = null;

    @JsonIgnore
    @Column(name = "oauth2_access_token", length = 512)
    private String oauth2AccessToken;

    @JsonIgnore
    @Column(name = "oauth2_refresh_token", length = 512)
    private String oauth2RefreshToken;

    @Column(name = "email", length = 512)
    @Size(max = 512)
    private String email;

    @Column(name = "provider_type", length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProviderType providerType;

    @Column(name = "role_type", length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull
    private RoleType roleType;

    @Column(name = "agreed_version", length = 10)
    private String agreedVersion;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "modified_at", insertable = false)
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @Column(name = "recent_login_at")
    private LocalDateTime recentLoginAt = LocalDateTime.now();

    @Column(name = "is_deleted")
    @NotNull
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_adult", nullable = false)
    private boolean isAdult;

    protected User(){}

    private User(
            String userId,
            ProviderType providerType,
            String email,
            String nickname,
            RoleType roleType) {
        this.userId = userId;
        this.providerType = providerType;
        this.roleType = roleType;
        this.email = email;
        this.userName = nickname;
    }

    public static User createUser(
            @NotNull String userId,
            @NotNull ProviderType providerType,
            @NotNull String email,
            @NotNull String nickname,
            @NotNull RoleType roleType){
        return new User(userId, providerType, email, nickname, roleType);
    }

    public static User createUser(
            @NotNull String userId,
            @NotNull ProviderType providerType,
            @NotNull String email,
            @NotNull String nickname,
            @NotNull RoleType roleType,
            @NotNull String accessToken,
            @NotNull String refreshToken
    ){
        User user = new User(userId, providerType, email, nickname, roleType);
        user.oauth2AccessToken = accessToken;
        user.oauth2RefreshToken = refreshToken;
        return user;
    }

    public static User from(PreSignup preSignup, String nickname){
        return new User(preSignup.getOauth2Id(),
                preSignup.getProviderType(),
                preSignup.getEmail(),
                nickname,
                RoleType.USER);
    }

    public void updateNickName(String nickName){
        this.userName = nickName;
    }

    public void updateWithdrawNickName(String nickName){
        this.withdrawUserName = nickName;
    }

    public void softDelete() {
        this.updateWithdrawNickName(this.userName);
        this.updateNickName(null);
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void updateToken(String accessToken, String refreshToken) {
        this.oauth2AccessToken = accessToken;
        this.oauth2RefreshToken = refreshToken;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateLoginAt(LocalDateTime now) {
        this.recentLoginAt = now;
    }
}
