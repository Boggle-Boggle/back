package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.entity.user.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    UserSettings findByUser(User savedUser);
}
