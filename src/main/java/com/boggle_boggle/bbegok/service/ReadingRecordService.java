package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.request.NewReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.request.UpdateReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.ReadingRecordResponse;
import com.boggle_boggle.bbegok.entity.*;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.*;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadingRecordService {
    private final ReadingRecordRepository readingRecordRepository;
    private final ReadingRecordLibraryMappingRepository mappingRepository;
    private final LibraryRepository libraryRepository;
    private final ReadDateRepository readDateRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookService bookService;

    public User getUser(String userId) {
        return userRepository.findByUserId(userId);
    }

    public void saveReadingRecord(NewReadingRecordRequest request, String userId) {
        //이미 해당 isbn이 저장되어있는지 확인 -> 없다면 새로 저장
        Book book = bookRepository.findByIsbn(request.getIsbn());
        if(book == null) {
            BookDetailResponse newBookData = bookService.getBook(request.getIsbn());
            if(newBookData == null) throw new GeneralException(Code.BOOK_NOT_FOUND);
            book = bookRepository.save(Book.createBook(newBookData));
        }

        //독서기록 저장 > 다대다(Library - mapping - readingRecord) 매핑 저장
        List<Library> libraries = new ArrayList<>();
        for (Long libraryId : request.getLibraryIdList()) {
            Library library = findLibrary(userId, libraryId);
            if (library == null) throw new GeneralException(Code.LIBRARY_NOT_FOUND);
            libraries.add(library);
        }

        ReadingRecord readingRecord = ReadingRecord.createReadingRecord(
                getUser(userId),
                book,
                request.getStartReadDate(),
                request.getEndReadDate(),
                libraries,
                request.getRating(),
                request.isVisible(),
                request.getReadStatus()
        );

        readingRecordRepository.save(readingRecord);
    }

    public ReadingRecordResponse getReadingRecord(Long id, String userId) {
        ReadingRecord readingRecord = findReadingRecord(id, userId);
        return ReadingRecordResponse.fromReadingRecord(readingRecord);
    }

    public Long getReadingRecordId(String isbn, String userId) {
        ReadingRecord readingRecord = findReadingRecord(isbn, userId);
        if(readingRecord == null) return null;
        return readingRecord.getReadingRecordSeq();
    }

    public void updateReadingRecord(Long id, UpdateReadingRecordRequest request, String userId) {
        ReadingRecord readingRecord = findReadingRecord(id, userId);

        //==날짜가 바뀌었다면 기존날짜 삭제후 업데이트
        if((request.getReadDateList() != null)) {
            readingRecord.getReadDateList().clear();
            readDateRepository.deleteAll(readingRecord.getReadDateList());
        }

        //==서재가 바뀌었다면 바뀌기 전/후 서재 찾아서 업데이트
        List<Library> libraries = new ArrayList<>();
        if(request.getLibraryIdList() != null) {
            readingRecord.getMappingList().clear();
            mappingRepository.deleteAll(readingRecord.getMappingList());
            for(Long libraryId : request.getLibraryIdList()) libraries.add(findLibrary(userId, libraryId));
        }

        readingRecord.update(request.getReadStatus(), request.getRating(), request.getReadDateList(),
                request.getIsVisible(), libraries);
    }

    public void deleteReadingRecord(Long id, String username) {
        readingRecordRepository.delete(findReadingRecord(id, username));
    }


    private ReadingRecord findReadingRecord(String isbn, String userId){
        Book book = bookRepository.findByIsbn(isbn);
        User user = getUser(userId);
        return readingRecordRepository.findByUserAndBook(user, book);
    }

    private ReadingRecord findReadingRecord(Long id, String userId){
        User user = getUser(userId);
        return readingRecordRepository.findByreadingRecordSeqAndUser(id, user)
                .orElseThrow(() -> new GeneralException(Code.READING_RECORD_NOT_FOUND));
    }

    private Library findLibrary(String userId, Long libraryId){
        User user = getUser(userId);
        return libraryRepository.findByUserAndLibrarySeq(user, libraryId)
                .orElseThrow(() -> new GeneralException(Code.LIBRARY_NOT_FOUND));
    }

}
