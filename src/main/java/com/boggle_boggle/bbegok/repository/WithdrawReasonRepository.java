package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.WithdrawReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawReasonRepository extends JpaRepository<WithdrawReason, Long> {
}