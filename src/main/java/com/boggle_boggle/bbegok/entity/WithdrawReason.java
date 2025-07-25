package com.boggle_boggle.bbegok.entity;

import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.WithdrawType;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class WithdrawReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long withdrawReasonSeq;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "withdraw_type")
    private WithdrawType withdrawType;

    @Column(length = 400)
    private String withdrawText;

    protected WithdrawReason() {}

    private WithdrawReason(User user, WithdrawType withdrawType, String withdrawText) {
        this.withdrawType = withdrawType;
        this.withdrawText = withdrawText;
        this.user = user;
    }

    public static WithdrawReason createWithdrawReason(User user, WithdrawType withdrawType, String withdrawText){
        return new WithdrawReason(user, withdrawType, withdrawText);
    }

}