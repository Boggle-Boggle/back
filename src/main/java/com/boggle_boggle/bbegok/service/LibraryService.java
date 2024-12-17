package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.LibrariesDto;
import com.boggle_boggle.bbegok.dto.LibraryBook;
import com.boggle_boggle.bbegok.dto.request.LibraryRequest;
import com.boggle_boggle.bbegok.dto.response.BookShelfResponse;
import com.boggle_boggle.bbegok.dto.response.LibraryBookListResponse;
import com.boggle_boggle.bbegok.dto.response.LibraryResponse;
import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.entity.Library;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.entity.user.UserSettings;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import com.boggle_boggle.bbegok.enums.SortingType;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.LibraryRepository;
import com.boggle_boggle.bbegok.repository.ReadingRecordLibraryMappingRepository;
import com.boggle_boggle.bbegok.repository.ReadingRecordRepository;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import com.boggle_boggle.bbegok.repository.user.UserSettingsRepository;
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
    private final UserSettingsRepository userSettingsRepository;
    private final ReadingRecordLibraryMappingRepository readingRecordLibraryMappingRepository;
    private final ReadingRecordRepository readingRecordRepository;

    public User getUser(String userId) {
        return userRepository.findByUserId(userId);
    }

    public Sort getSortWithReadingRecordToLibraryMapping(User user) {
        switch (userSettingsRepository.findByUser(user).getSortingType()) {
            case newest_first:
                return Sort.by(Sort.Direction.DESC, "readingRecord.readingRecordSeq");
            case oldest_first:
                return Sort.by(Sort.Direction.ASC, "readingRecord.readingRecordSeq");
            default:
                return Sort.by(Sort.Direction.DESC, "readingRecord.rating");
        }
    }

    public Sort getSortWithReadingRecord(User user) {
        switch (userSettingsRepository.findByUser(user).getSortingType()) {
            case newest_first:
                return Sort.by(Sort.Direction.DESC, "readingRecordSeq");
            case oldest_first:
                return Sort.by(Sort.Direction.ASC, "readingRecordSeq");
            default:
                return Sort.by(Sort.Direction.DESC, "rating");
        }
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

    public LibraryBookListResponse findByLibraryId(Long libraryId, int pageNum, String userId, int pageSize, String keyword) {
        Library library = libraryRepository.findByUserAndLibrarySeq(getUser(userId), libraryId)
                .orElseThrow(() -> new GeneralException(Code.LIBRARY_NOT_FOUND));
        User user = getUser(userId);
        Pageable pageable = PageRequest.of(pageNum-1, pageSize, getSortWithReadingRecordToLibraryMapping(user));

        Page<ReadingRecord> booksPage;
        if(keyword == null) booksPage = readingRecordLibraryMappingRepository.findBooksByLibraryAndUser(library, user, pageable);
        else booksPage = readingRecordLibraryMappingRepository.findBooksByLibraryAndUserAndKeyword(library, user, keyword, pageable);



        return LibraryBookListResponse.fromPage(booksPage);
    }

    public LibraryBookListResponse findByStatus(ReadStatus status, int pageNum, String userId, int pageSize, String keyword) {
        User user = getUser(userId);
        Pageable pageable = PageRequest.of(pageNum-1, pageSize, getSortWithReadingRecord(user));

        Page<ReadingRecord> booksPage;
        if(keyword == null) booksPage = readingRecordRepository.findBooksByUserAndStatus(status, user, pageable);
        else booksPage = readingRecordRepository.findBooksByUserAndStatusAndKeyword(status, user, keyword, pageable);

        return LibraryBookListResponse.fromPage(booksPage);
    }

    public LibraryBookListResponse findAll(int pageNum, String userId, int pageSize, String keyword) {
        User user = getUser(userId);
        Pageable pageable = PageRequest.of(pageNum-1, pageSize, getSortWithReadingRecord(user));
        Page<ReadingRecord> booksPage;

        if(keyword == null) booksPage = readingRecordRepository.findBooksWithReadingRecordIdByUser(user, pageable);
        else booksPage = readingRecordRepository.findBooksWithReadingRecordIdByUserAndKeyword(user, keyword, pageable);
        return LibraryBookListResponse.fromPage(booksPage);
    }


    public BookShelfResponse findBookshelfByEndDate(Integer year, Integer month, String userId) {
        User user = getUser(userId);
        List<ReadingRecord> booksPage = readingRecordRepository.findBooksByUserAndReadDate(user, year, month);
        return BookShelfResponse.fromPage(booksPage);
    }
}
