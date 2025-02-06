package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.LibrariesDto;
import com.boggle_boggle.bbegok.dto.LibraryBook;
import com.boggle_boggle.bbegok.dto.ReadingRecordAndDateDTO;
import com.boggle_boggle.bbegok.dto.RecordByStatusDto;
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

    public User getUser(String userSeq) {
        User user = userRepository.findByUserSeqAndIsDeleted(Long.valueOf(userSeq), false);
        if(user == null) {
            //탈퇴한 적 있는 회원
            if(userRepository.countByUserSeqAndIsDeleted(Long.valueOf(userSeq), true) > 0) throw new GeneralException(Code.USER_ALREADY_WITHDRAWN);
            else throw new GeneralException(Code.USER_NOT_FOUND);
        }
        return user;
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

    public LibraryResponse getLibraries(String userSeq) {
        User user = getUser(userSeq);
        List<LibrariesDto> librariesDtos = libraryRepository.findAllByUserWithBookCount(user);
        List<RecordByStatusDto> readingRecords = readingRecordRepository.countReadingRecordsByStatus(user);
        return LibraryResponse.ofDtos(librariesDtos, readingRecords);
    }

    public void saveNewLibrary(LibraryRequest request, String userSeq) {
        // 중복 체크
        if (libraryRepository.existsByLibraryNameAndUser(request.getLibraryName(),getUser(userSeq))) {
            throw new GeneralException(Code.DUPLICATE_LIBRARY_NAME);
        }

        Library newLibrary = Library.createLibrary(getUser(userSeq), request.getLibraryName());
        libraryRepository.save(newLibrary);
    }


    public void deleteLibrary(Long libraryId, String userSeq) {
        Library library = libraryRepository.findByUserAndLibrarySeq(getUser(userSeq), libraryId)
                .orElseThrow(() -> new GeneralException(Code.LIBRARY_NOT_FOUND));

        libraryRepository.delete(library);
    }

    public LibraryBookListResponse findByLibraryId(Long libraryId, int pageNum, String userSeq, int pageSize, String keyword) {
        Library library = libraryRepository.findByUserAndLibrarySeq(getUser(userSeq), libraryId)
                .orElseThrow(() -> new GeneralException(Code.LIBRARY_NOT_FOUND));
        User user = getUser(userSeq);
        Pageable pageable = PageRequest.of(pageNum-1, pageSize, getSortWithReadingRecordToLibraryMapping(user));

        Page<ReadingRecord> booksPage;
        if(keyword == null) booksPage = readingRecordLibraryMappingRepository.findBooksByLibraryAndUser(library, user, pageable);
        else booksPage = readingRecordLibraryMappingRepository.findBooksByLibraryAndUserAndKeyword(library, user, keyword, pageable);



        return LibraryBookListResponse.fromPage(booksPage);
    }

    public LibraryBookListResponse findByStatus(ReadStatus status, int pageNum, String userSeq, int pageSize, String keyword) {
        User user = getUser(userSeq);
        Pageable pageable = PageRequest.of(pageNum-1, pageSize, getSortWithReadingRecord(user));

        Page<ReadingRecord> booksPage;
        if(keyword == null) booksPage = readingRecordRepository.findBooksByUserAndStatus(status, user, pageable);
        else booksPage = readingRecordRepository.findBooksByUserAndStatusAndKeyword(status, user, keyword, pageable);

        return LibraryBookListResponse.fromPage(booksPage);
    }

    public LibraryBookListResponse findAll(int pageNum, String userSeq, int pageSize, String keyword) {
        User user = getUser(userSeq);
        Pageable pageable = PageRequest.of(pageNum-1, pageSize, getSortWithReadingRecord(user));
        Page<ReadingRecord> booksPage;

        if(keyword == null) booksPage = readingRecordRepository.findBooksWithReadingRecordIdByUser(user, pageable);
        else booksPage = readingRecordRepository.findBooksWithReadingRecordIdByUserAndKeyword(user, keyword, pageable);
        return LibraryBookListResponse.fromPage(booksPage);
    }


    public BookShelfResponse findBookshelfByEndDate(Integer year, Integer month, String userSeq) {
        User user = getUser(userSeq);
        List<ReadingRecordAndDateDTO> booksPage = readingRecordRepository.findBooksByUserAndReadDate(user, year, month, ReadStatus.completed);
        return BookShelfResponse.fromPage(booksPage);
    }
}
