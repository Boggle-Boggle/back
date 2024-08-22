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

    @Column(name = "user_id", length = 64, unique = true)
    @NotNull
    @Size(max = 64)
    private String userId;

    @Column(name = "user_name", unique = true, length = 20)
    private String userName = null;

    @JsonIgnore
    @Column(name = "password", length = 128)
    @Size(max = 128)
    private String password;

    @Column(name = "email", length = 512, unique = true, nullable = true)
    @Size(max = 512)
    private String email;

    @Column(name = "email_verified_yn", length = 1)
    @Size(min = 1, max = 1)
    private String emailVerifiedYn;

    @Column(name = "profile_image_url", length = 512, nullable = true)
    private String profileImageUrl = null;

    @Column(name = "provider_type", length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProviderType providerType;

    @Column(name = "role_type", length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull
    private RoleType roleType;

    @Column(name = "is_agree_to_terms")
    @NotNull
    private Boolean isAgreeToTerms = false;

    @Column(name = "created_at")
    @NotNull
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    @NotNull
    private LocalDateTime modifiedAt;


    protected User(){}

    protected User(
            String userId,
            String emailVerifiedYn,
            ProviderType providerType,
            RoleType roleType,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt) {
        this.userId = userId;
        this.emailVerifiedYn = emailVerifiedYn;
        this.providerType = providerType;
        this.roleType = roleType;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }


    public static User createUser(
            @NotNull @Size(max = 64) String userId,
            @NotNull @Size(max = 1) String emailVerifiedYn,
            @NotNull ProviderType providerType,
            @NotNull RoleType roleType,
            @NotNull LocalDateTime createdAt,
            @NotNull LocalDateTime modifiedAt){
        return new User(userId, emailVerifiedYn, providerType, roleType, createdAt, modifiedAt);
    }

    public void updateNickName(String nickName){
        this.userName = nickName;
    }

    public void agreeToTerms(){
        this.isAgreeToTerms = true;
        this.roleType = RoleType.USER;
    }
}
