package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TermsJpaRepository extends JpaRepository<Terms, Long> {
    List<Terms> findByVersion(String version);
}
