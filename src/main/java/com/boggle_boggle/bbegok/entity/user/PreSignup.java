package com.boggle_boggle.bbegok.entity.user;

import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class PreSignup {
    @Id
    @Column(name = "pre_signup_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preSignupSeq;

    @Column(name = "provider_type", length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProviderType providerType;

    @Column(name = "oauth2_id", length = 64)
    @NotNull
    @Size(max = 64)
    private String oauth2Id;

    @Column(name = "email", length = 64)
    @NotNull
    private String email;

    @Column(updatable = false, name = "created_at")
    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(updatable = false, name = "expires_at")
    @NotNull
    private LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);

    protected PreSignup() {}

    private final int duration = 30;
    private PreSignup(ProviderType providerType, String oauth2Id, String email, LocalDateTime now) {
        this.providerType = providerType;
        this.oauth2Id = oauth2Id;
        this.email = email;
        this.createdAt = now;
        this.expiresAt = now.plusMinutes(duration);
    }

    public static PreSignup createPreSignup(String oauth2Id, String email, ProviderType providerType) {
        return new PreSignup(providerType, oauth2Id, email, LocalDateTime.now());
    }
}
