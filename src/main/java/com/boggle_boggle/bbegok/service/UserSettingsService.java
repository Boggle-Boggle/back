package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.entity.user.UserSettings;
import com.boggle_boggle.bbegok.enums.SortingType;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.UserRepository;
import com.boggle_boggle.bbegok.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSettingsService {
    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;

    public User getUser(String userSeq) {
        User user = userRepository.findByUserSeqAndIsDeleted(Long.valueOf(userSeq), false);
        if(user == null) {
            //탈퇴한 적 있는 회원
            if(userRepository.countByUserSeqAndIsDeleted(Long.valueOf(userSeq), true) > 0) throw new GeneralException(Code.USER_ALREADY_WITHDRAWN);
            else throw new GeneralException(Code.USER_NOT_FOUND);
        }
        return user;
    }

    public String getSortingType(String userSeq) {
        User user = getUser(userSeq);
        return userSettingsRepository.findByUser(user).getSortingType().toString();
    }

    public void updateSortingType(String userSeq, SortingType sortingType) {
        User user = getUser(userSeq);
        UserSettings userSettings = userSettingsRepository.findByUser(user);
        userSettings.updateSortingType(sortingType);
    }
}
