package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.AgreeToTerms;
import com.boggle_boggle.bbegok.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AgreeToTermsRepository extends JpaRepository<AgreeToTerms, Long> {

    //최근에 동의한 약관의 버전을 리턴
    @Query("SELECT at.terms.version " +
            "FROM AgreeToTerms at " +
            "WHERE at.user.userId = :userId " +
            "ORDER BY at.agreeDate DESC")
    Optional<List<String>> findLatestAgreedTermsVersionByUserId(@Param("userId") String userId);
}