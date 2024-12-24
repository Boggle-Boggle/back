package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.request.NewReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.request.UpdateReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.ReadingRecordResponse;
import com.boggle_boggle.bbegok.entity.*;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.*;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import com.boggle_boggle.bbegok.utils.LocalDateTimeUtil;
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

    //다읽은 경우만 필수란이 있으므로 검사한다.
    private void validationNewReadingRecordRequest(NewReadingRecordRequest request) {
        ReadStatus status = request.getReadStatus();
        if(status == ReadStatus.completed) {
            if(request.getRating() == null || request.getIsVisible() == null ||
            request.getStartReadDate() == null || request.getEndReadDate() == null) throw new GeneralException(Code.BAD_REQUEST, "Required value is missing.");
            if(!LocalDateTimeUtil.isStartBeforeEnd(request.getStartReadDate(), request.getEndReadDate())) throw new GeneralException(Code.INVALID_READING_DATE);
        }
    }


    public Long saveReadingRecord(NewReadingRecordRequest request, String userId) {
        //유효성 검사
        validationNewReadingRecordRequest(request);

        //이미 해당 isbn이 저장되어있는지 확인 -> 없다면 새로 저장
        Book book = bookRepository.findByIsbn(request.getIsbn());
        if(book == null) {
            BookDetailResponse newBookData = bookService.getBook(request.getIsbn());
            if(newBookData == null) throw new GeneralException(Code.BOOK_NOT_FOUND);
            book = bookRepository.save(Book.createBook(newBookData));
        }
        //해당 책에대한 독서기록이 이전에 있었는지 확인 -> 이전에 있었다면 에러
        if(findReadingRecord(book.getIsbn(), userId) != null) throw new GeneralException(Code.READING_RECORD_ALREADY_EXIST);

        //독서기록 저장 > 다대다(Library - mapping - readingRecord) 매핑 저장
        List<Library> libraries = new ArrayList<>();
        if(request.getLibraryIdList() != null && !request.getLibraryIdList().isEmpty()) {
            for (Long libraryId : request.getLibraryIdList()) {
                Library library = findLibrary(userId, libraryId);
                if (library == null) throw new GeneralException(Code.LIBRARY_NOT_FOUND);
                libraries.add(library);
            }
        }

        ReadingRecord readingRecord = ReadingRecord.createReadingRecord(
                getUser(userId),
                book,
                request.getStartReadDate(),
                request.getEndReadDate(),
                libraries,
                request.getRating(),
                request.getIsVisible(),
                request.getReadStatus()
        );

        ReadingRecord savedReadingRecord = readingRecordRepository.save(readingRecord);
        return savedReadingRecord.getReadingRecordSeq();
    }



    public ReadingRecordResponse getReadingRecord(Long id, String userId) {
        ReadingRecord readingRecord = findReadingRecord(id, userId);
        return ReadingRecordResponse.fromEntity(readingRecord);
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
