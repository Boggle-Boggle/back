package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.client.AladinClient;
import com.boggle_boggle.bbegok.config.openfeign.OpenFeignConfig;
import com.boggle_boggle.bbegok.dto.*;
import com.boggle_boggle.bbegok.dto.request.CustomBookRecordRequest;
import com.boggle_boggle.bbegok.dto.request.NewReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.request.NormalBookRecordRequest;
import com.boggle_boggle.bbegok.dto.request.UpdateReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.EditReadingRecordResponse;
import com.boggle_boggle.bbegok.dto.response.ReadingRecordIdResponse;
import com.boggle_boggle.bbegok.dto.response.ReadingRecordResponse;
import com.boggle_boggle.bbegok.entity.*;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.*;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import com.boggle_boggle.bbegok.utils.LocalDateTimeUtil;
import lombok.Locked;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadingRecordService {
    private final ReadingRecordRepository readingRecordRepository;
    private final ReadDateRepository readDateRepository;
    private final ReadingRecordLibraryMappingRepository mappingRepository;
    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookService bookService;
    private final QueryService queryService;

    private User getUser(String userSeq) {
        return queryService.getUser(userSeq);
    }

    @Transactional
    public ReadingRecordIdResponse save(NewReadingRecordRequest request, String userSeq) {
        //책(커스텀or알라딘)과 독서기록을 연결해 저장
        User user = getUser(userSeq);
        Book book = bookService.saveBookByRequest(request, user);
        ReadingRecord record = saveRecord(book, user, request);
        return ReadingRecordIdResponse.of(record.getReadingRecordSeq());
    }

    private ReadingRecord saveRecord(Book book, User user, NewReadingRecordRequest request) {
        //해당 책에대한 독서기록이 이전에 있었는지 확인 -> 이전에 있었다면 에러
        Optional<ReadingRecord> readingRecord = readingRecordRepository.findByUserAndBook(user, book);
        if (readingRecord.isPresent()) {
            throw new GeneralException(
                    Code.READING_RECORD_ALREADY_EXIST,
                    Map.of("readingRecordId", readingRecord.get().getReadingRecordSeq())
            );
        }
        List<Library> libraries = queryService.getLibraries(request.getLibraryIdList());
        return readingRecordRepository.save(ReadingRecord.createReadingRecord(user, book, request, libraries));
    }


    public ReadingRecordResponse getReadingRecord(Long id, String userSeq) {
        ReadingRecord readingRecord = findReadingRecord(id, userSeq);
        return ReadingRecordResponse.fromEntity(readingRecord);
    }

    public EditReadingRecordResponse getEditReadingRecord(Long readingRecordId, String userSeq) {
        User user = getUser(userSeq);
        ReadingRecord readingRecord = findReadingRecord(readingRecordId, userSeq);
        List<RecordLibraryListDto> recordLibraryListDtos = libraryRepository.findRecordLibraryListDtosInfoByUser(readingRecord, user);
        return EditReadingRecordResponse.from(readingRecord, recordLibraryListDtos);
    }

    public Long getReadingRecordId(String isbn, String userSeq) {
        ReadingRecord readingRecord = findByIsbn(isbn, userSeq);
        if(readingRecord == null) return null;
        return readingRecord.getReadingRecordSeq();
    }

    public void updateReadingRecord(Long id, UpdateReadingRecordRequest request, String userSeq) {
        ReadingRecord readingRecord = findReadingRecord(id, userSeq);
        updateReadingRecord(request, getUser(userSeq), readingRecord);
    }

    public void deleteReadingRecord(Long id, String username) {
        readingRecordRepository.delete(findReadingRecord(id, username));
    }


    private ReadingRecord findByIsbn(String isbn, String userSeq){
        Book book = bookRepository.findByIsbnAndIsCustomFalse(isbn)
                .orElseThrow(() -> new GeneralException(Code.BOOK_NOT_FOUND));
        User user = getUser(userSeq);
        return readingRecordRepository.findByUserAndBook(user, book)
                .orElseThrow(() -> new GeneralException(Code.READING_RECORD_NOT_FOUND));
    }

    private ReadingRecord findReadingRecord(Long id, String userSeq){
        User user = getUser(userSeq);
        return readingRecordRepository.findByreadingRecordSeqAndUserOrderByReadingRecordSeq(id, user)
                .orElseThrow(() -> new GeneralException(Code.READING_RECORD_NOT_FOUND));
    }

    private Library findLibrary(String userSeq, Long libraryId){
        User user = getUser(userSeq);
        return libraryRepository.findByUserAndLibrarySeq(user, libraryId)
                .orElseThrow(() -> new GeneralException(Code.LIBRARY_NOT_FOUND));
    }

    private void updateReadingRecord(UpdateReadingRecordRequest request, User user, ReadingRecord readingRecord) {
        if(request.getReadDateList().isPresent()) {
            if(request.getReadDateList().get() == null) throw new GeneralException(Code.BAD_REQUEST, "readDateIdList can't null");


            List<ReadDateAndIdDto> readDateAndIdDtoList = request.getReadDateList().get();

            //기존 ReadDate중에서 요청readDate에 없는경우 해당 readDate를 삭제해야하는데,
            //1. id가 있으면 업데이트처리 -> 2. id가 없으면 새로운 회독정보 추가 -> 3. 원래 DB와 비교했을때 1,2에 해당하지 않은 정보들은 삭제
            List<ReadDate> readDateList = readingRecord.getReadDateList();
            Set<Long> set = new HashSet<>();

            //(주의) readDateList가 빈 배열일 경우, 위시리스트 형태로 변경한다. (기존 회독정보 전부 삭제 -> 위시 ReadDate만 생성)
//            if(readDateAndIdDtoList.isEmpty()) {
//                ReadDate readDate = readDateRepository.save(ReadDate.createReadDate(readingRecord, null, null, ReadStatus.pending));
//                set.add(readDate.getReadDateSeq());
//            }
            for(ReadDateAndIdDto dto : readDateAndIdDtoList) {
                if(dto.getReadDateId() != null) { //날짜 업데이트 및 set에 저장
                    set.add(dto.getReadDateId());
                    ReadDate readDate = readDateRepository.findById(dto.getReadDateId())
                            .orElseThrow(()-> new GeneralException(Code.READ_DATE_NOT_FOUND));
                    readDate.update(dto.getStartReadDate(), dto.getEndReadDate(), dto.getStatus());
                } else { //새로운 날짜 생성 및 set에 저장
                    ReadDate readDate = readDateRepository.save(ReadDate.createReadDate(readingRecord, dto.getStartReadDate(), dto.getEndReadDate(),dto.getStatus()));
                    set.add(readDate.getReadDateSeq());
                }
            }

            Iterator<ReadDate> iterator = readDateList.iterator();
            while (iterator.hasNext()) {
                ReadDate readDate = iterator.next();
                if (set.contains(readDate.getReadDateSeq())) continue;

                readDate.removeNoteAssociation(); // Note와의 연관관계 해제
                iterator.remove(); // readDateList에서 안전하게 삭제
                readDateRepository.delete(readDate); // ReadDate 삭제
            }

        }
        if(request.getLibraryIdList().isPresent()) {
            if(request.getLibraryIdList().get() == null) throw new GeneralException(Code.BAD_REQUEST, "libraryIdList can't null");
            //전부끊고 재연결처리
            readingRecord.getMappingList().clear();
            mappingRepository.deleteAll(readingRecord.getMappingList());
            List<Library> libraries = new ArrayList<>();
            for(Long libraryId : request.getLibraryIdList().get()) {
                libraries.add(findLibrary(String.valueOf(user.getUserSeq()), libraryId));
            }
            readingRecord.addLibraries(libraries);
        }
        if(request.getRating().isPresent()) readingRecord.updateRating(request.getRating().get());
        if(request.getIsVisible().isPresent()) {
            if(request.getIsVisible().get() == null) throw new GeneralException(Code.BAD_REQUEST, "visible can't null");
            readingRecord.updateIsVisible(request.getIsVisible().get());
        }

    }

    public List<ReadDateIndexDto> getReadDates(Long readingRecordId, String userSeq) {
        List<ReadDate> readDateList = readDateRepository.findByReadingRecordOrderByReadDateSeq(findReadingRecord(readingRecordId, userSeq));
        return IntStream.range(0, readDateList.size())
                .mapToObj(i -> new ReadDateIndexDto(readDateList.get(i), i))
                .toList();
    }

}
