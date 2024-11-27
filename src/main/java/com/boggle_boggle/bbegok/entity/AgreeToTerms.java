package com.boggle_boggle.bbegok.entity;

import com.boggle_boggle.bbegok.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
public class AgreeToTerms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agreeTermsSeq;

    @Column(name = "agree_date", nullable = false)
    private LocalDateTime agreeDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terms_seq")
    private Terms terms;
}
