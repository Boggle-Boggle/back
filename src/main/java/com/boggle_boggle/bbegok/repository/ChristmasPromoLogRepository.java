package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.ChristmasPromoLog;
import com.boggle_boggle.bbegok.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChristmasPromoLogRepository extends JpaRepository<ChristmasPromoLog, Long> {
    List<ChristmasPromoLog> findByUser(User user);
    long countByUser(User user);
}
