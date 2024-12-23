package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.entity.user.UserSettings;
import com.boggle_boggle.bbegok.enums.SortingType;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import com.boggle_boggle.bbegok.repository.user.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSettingsService {
    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;

    public User getUser(String userId) {
        return userRepository.findByUserId(userId);
    }

    public String getSortingType(String userId) {
        User user = getUser(userId);
        return userSettingsRepository.findByUser(user).getSortingType().toString();
    }

    public void updateSortingType(String userId, SortingType sortingType) {
        User user = getUser(userId);
        UserSettings userSettings = userSettingsRepository.findByUser(user);
        userSettings.updateSortingType(sortingType);
    }
}
