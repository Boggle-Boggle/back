package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermsJpaRepository extends JpaRepository<Terms, Long> {
    List<Terms> findByVersion(String version);
}
