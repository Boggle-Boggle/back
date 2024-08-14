package com.boggle_boggle.bbegok.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(name = "refresh_token", length = 256)
    private String refreshToken;

    protected UserRefreshToken(){}

    protected UserRefreshToken(
            User user,
            String refreshToken ) {
        this.user = user;
        this.userId = user.getUserId();
        this.refreshToken = refreshToken;
    }

    public static UserRefreshToken createUserRefreshToken(
            User user,
            @NotNull @Size(max = 256) String refreshToken){
        return new UserRefreshToken(user, refreshToken);
    }
}
