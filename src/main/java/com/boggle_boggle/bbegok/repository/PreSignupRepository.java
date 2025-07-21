package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.user.PreSignup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreSignupRepository extends JpaRepository<PreSignup, Long> {
}
