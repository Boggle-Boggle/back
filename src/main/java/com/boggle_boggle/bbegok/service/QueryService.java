package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.entity.Library;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.LibraryRepository;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QueryService {
    private final LibraryRepository libraryRepository;
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

    public List<Library> getLibraries(List<Long> idList) {
        List<Library> libraries = libraryRepository.findAllById(idList);
        if (libraries.size() != idList.size()) {
            throw new GeneralException(Code.LIBRARY_NOT_FOUND);
        }
        return libraries;
    }
}
