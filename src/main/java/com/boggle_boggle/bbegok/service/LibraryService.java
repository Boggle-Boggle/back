package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.LibrariesDto;
import com.boggle_boggle.bbegok.dto.request.LibraryRequest;
import com.boggle_boggle.bbegok.dto.response.LibraryBookListResponse;
import com.boggle_boggle.bbegok.dto.response.LibraryResponse;
import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.entity.Library;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.LibraryRepository;
import com.boggle_boggle.bbegok.repository.ReadingRecordLibraryMappingRepository;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.List;

import static com.boggle_boggle.bbegok.dto.response.LibraryResponse.ofLibrariesDto;

@Service
@RequiredArgsConstructor
@Transactional
public class LibraryService {
    private final int PAGE_SIZE = 30;
    private final LibraryRepository libraryRepository;
    private final UserRepository userRepository;
    private final ReadingRecordLibraryMappingRepository readingRecordLibraryMappingRepository;

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

    public LibraryBookListResponse findByLibraryId(Long libraryId, int pageNum, String userId) {
        Library library = libraryRepository.findByUserAndLibrarySeq(getUser(userId), libraryId)
                .orElseThrow(() -> new GeneralException(Code.LIBRARY_NOT_FOUND));
        User user = getUser(userId);
        Pageable pageable = PageRequest.of(pageNum-1, PAGE_SIZE);

        Page<Book> booksPage = readingRecordLibraryMappingRepository.findBooksByLibraryAndUser(library, user, pageable);
        return LibraryBookListResponse.fromPage(booksPage);
    }

    public LibraryBookListResponse findByStatus(ReadStatus status, int pageNum, String userId) {
        User user = getUser(userId);
        Pageable pageable = PageRequest.of(pageNum-1, PAGE_SIZE);

        Page<Book> booksPage = readingRecordLibraryMappingRepository.findBooksByUserAndStatus(status, user, pageable);
        return LibraryBookListResponse.fromPage(booksPage);
    }

    public LibraryBookListResponse findAll(int pageNum, String userId) {
        User user = getUser(userId);
        Pageable pageable = PageRequest.of(pageNum-1, PAGE_SIZE);
        Page<Book> booksPage = readingRecordLibraryMappingRepository.findBooksByUser(user, pageable);
        return LibraryBookListResponse.fromPage(booksPage);
    }
}
