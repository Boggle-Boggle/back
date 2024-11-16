package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.LibrariesDto;
import com.boggle_boggle.bbegok.dto.request.LibraryRequest;
import com.boggle_boggle.bbegok.dto.response.LibraryResponse;
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

import static com.boggle_boggle.bbegok.dto.response.LibraryResponse.ofLibrariesDto;

@Service
@RequiredArgsConstructor
@Transactional
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final UserRepository userRepository;

    public User getUser(String userId) {
        return userRepository.findByUserId(userId);
    }

    public List<LibraryResponse> getLibraries(String userId) {
        List<LibrariesDto> librariesDtos = libraryRepository.findAllByUserWithBookCount(getUser(userId));
        return librariesDtos.stream()
                .map(LibraryResponse::ofLibrariesDto).toList();
    }

    public void saveNewLibrary(LibraryRequest request, String userId) {
        // 중복 체크
        if (libraryRepository.existsByLibraryNameAndUser(request.getLibraryName(),getUser(userId))) {
            throw new GeneralException(Code.DUPLICATE_LIBRARY_NAME);
        }

        Library newLibrary = Library.createLibrary(getUser(userId), request.getLibraryName());
        libraryRepository.save(newLibrary);
    }


    public void deleteLibrary(Long libraryId, String userId) {
        Library library = libraryRepository.findByUserAndLibrarySeq(getUser(userId), libraryId)
                .orElseThrow(() -> new GeneralException(Code.LIBRARY_NOT_FOUND));

        libraryRepository.delete(library);
    }
}
