package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.ReadDateAndIdDto;
import com.boggle_boggle.bbegok.dto.ReadDateIndexDto;
import com.boggle_boggle.bbegok.dto.RecordLibraryListDto;
import com.boggle_boggle.bbegok.dto.request.NewReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.request.UpdateReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.EditReadingRecordResponse;
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

    public User getUser(String userId) {
        User user = userRepository.findByUserIdAndIsDeleted(userId, false);
        if(user == null) {
            //탈퇴한 적 있는 회원
            if(userRepository.countByUserIdAndIsDeleted(userId, true) > 0) throw new GeneralException(Code.USER_ALREADY_WITHDRAWN);
            else throw new GeneralException(Code.USER_NOT_FOUND);
        }
        return user;
    }

    private void validationNewReadingRecordRequest(NewReadingRecordRequest request) {
        switch (request.getReadStatus()) {
            case completed: //별점, 시작/종료일 필수
                if(request.getRating() == null || request.getIsVisible() == null ||
                        request.getStartReadDate() == null || request.getEndReadDate() == null) {
                    throw new GeneralException(Code.BAD_REQUEST, "Required value is missing.");
                }
                if(!LocalDateTimeUtil.isStartBeforeEnd(request.getStartReadDate(), request.getEndReadDate())) throw new GeneralException(Code.INVALID_READING_DATE);
                break;

            case reading: //시작일 필수, 종료일/별점 공란
                if(request.getStartReadDate() == null) throw new GeneralException(Code.BAD_REQUEST, "start-read-date is missing");
                if(request.getEndReadDate() != null || request.getRating() != null) throw  new GeneralException(Code.BAD_REQUEST, "End-date and rating cannot be set in the Reading status.");
                break;

            case pending: //시작/종료일/별점 공란
                if(request.getStartReadDate() != null || request.getEndReadDate() != null || request.getRating() != null) {
                    throw  new GeneralException(Code.BAD_REQUEST, "Date and rating cannot be set in the Pending status.");
                }
                break;

            default:
                throw new GeneralException(Code.BAD_REQUEST, "Invalid read status.");
        }

    }

    private void validationReadDateAndIdDto(ReadDateAndIdDto readDateAndIdDto) {
        switch (readDateAndIdDto.getStatus()) {
            case completed:
                if(readDateAndIdDto.getStartReadDate() == null || readDateAndIdDto.getEndReadDate() == null) throw new GeneralException(Code.BAD_REQUEST, "Required value is missing.");
                if(!LocalDateTimeUtil.isStartBeforeEnd(readDateAndIdDto.getStartReadDate(), readDateAndIdDto.getEndReadDate())) throw new GeneralException(Code.INVALID_READING_DATE);
                break;

            case reading:
                if(readDateAndIdDto.getStartReadDate() == null) throw new GeneralException(Code.BAD_REQUEST, "start-read-date is missing");
                if(readDateAndIdDto.getEndReadDate() != null) throw  new GeneralException(Code.BAD_REQUEST, "End-date and rating cannot be set in the Reading status.");
                break;

            case pending:
                throw new GeneralException(Code.BAD_REQUEST, "Cannot update while in pending status.");

            default:
                throw new GeneralException(Code.BAD_REQUEST, "Invalid read status.");
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
                request.getIsVisible() == null ? true:request.getIsVisible(),
                request.getReadStatus()
        );

        ReadingRecord savedReadingRecord = readingRecordRepository.save(readingRecord);
        return savedReadingRecord.getReadingRecordSeq();
    }


    public ReadingRecordResponse getReadingRecord(Long id, String userId) {
        ReadingRecord readingRecord = findReadingRecord(id, userId);
        return ReadingRecordResponse.fromEntity(readingRecord);
    }

    public EditReadingRecordResponse getEditReadingRecord(Long readingRecordId, String userId) {
        User user = getUser(userId);
        ReadingRecord readingRecord = findReadingRecord(readingRecordId, userId);
        List<RecordLibraryListDto> recordLibraryListDtos = libraryRepository.findRecordLibraryListDtosInfoByUser(readingRecord, user);
        return EditReadingRecordResponse.from(readingRecord, recordLibraryListDtos);
    }

    public Long getReadingRecordId(String isbn, String userId) {
        ReadingRecord readingRecord = findReadingRecord(isbn, userId);
        if(readingRecord == null) return null;
        return readingRecord.getReadingRecordSeq();
    }

    public void updateReadingRecord(Long id, UpdateReadingRecordRequest request, String userId) {
        //ReadDate에 대한 유효성 검사
        if(request.getReadDateList().isPresent()) {
            if(request.getReadDateList().get() == null) throw new GeneralException(Code.BAD_REQUEST, "readDateIdList can't null");
            else if(!request.getReadDateList().get().isEmpty()) {
                for(ReadDateAndIdDto readDateAndIdDto : request.getReadDateList().get()) validationReadDateAndIdDto(readDateAndIdDto);
            }
        }
        ReadingRecord readingRecord = findReadingRecord(id, userId);
        updateReadingRecord(request, getUser(userId), readingRecord);
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
        return readingRecordRepository.findByreadingRecordSeqAndUserOrderByReadingRecordSeq(id, user)
                .orElseThrow(() -> new GeneralException(Code.READING_RECORD_NOT_FOUND));
    }

    private Library findLibrary(String userId, Long libraryId){
        User user = getUser(userId);
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
            if(readDateAndIdDtoList.isEmpty()) {
                ReadDate readDate = readDateRepository.save(ReadDate.createReadDate(readingRecord, null, null, ReadStatus.pending));
                set.add(readDate.getReadDateSeq());
            }
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
                libraries.add(findLibrary(user.getUserId(), libraryId));
            }
            readingRecord.addLibraries(libraries);
        }
        if(request.getRating().isPresent()) readingRecord.updateRating(request.getRating().get());
        if(request.getIsVisible().isPresent()) {
            if(request.getIsVisible().get() == null) throw new GeneralException(Code.BAD_REQUEST, "visible can't null");
            readingRecord.updateIsVisible(request.getIsVisible().get());
        }

    }

    public List<ReadDateIndexDto> getReadDates(Long readingRecordId, String userId) {
        List<ReadDate> readDateList = readDateRepository.findByReadingRecordAndStatusNotOrderByReadDateSeq(findReadingRecord(readingRecordId, userId), ReadStatus.pending);
        return IntStream.range(0, readDateList.size())
                .mapToObj(i -> new ReadDateIndexDto(readDateList.get(i), i))
                .toList();
    }

}
