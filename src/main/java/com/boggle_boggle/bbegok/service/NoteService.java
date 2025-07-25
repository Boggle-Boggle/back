package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.NoteDto;
import com.boggle_boggle.bbegok.dto.PagesDto;
import com.boggle_boggle.bbegok.dto.ReadDateAndIdDto;
import com.boggle_boggle.bbegok.dto.request.NewNoteRequest;
import com.boggle_boggle.bbegok.dto.response.NotesByReadDateResponse;
import com.boggle_boggle.bbegok.entity.*;
import com.boggle_boggle.bbegok.entity.embed.Pages;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.*;
import com.boggle_boggle.bbegok.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteService {
    private final NoteRepository noteRepository;
    private final ReadingRecordRepository readingRecordRepository;
    private final ReadingRecordLibraryMappingRepository mappingRepository;
    private final LibraryRepository libraryRepository;
    private final ReadDateRepository readDateRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookService bookService;

    public List<NotesByReadDateResponse> getNote(Long recordId, String userSeq) {
        User user = getUser(userSeq);
        ReadingRecord readingRecord = findReadingRecord(recordId, userSeq);

        //현재 독서기록에 대한 모든 Note를 찾는다. 이때 readDateSeq별로 그룹바이 해야하고, readDateSeq순서대로 정렬(Null이면 제일 앞으로)
        List<Note> notes = noteRepository.findByReadingRecordAndReadingRecord_User(readingRecord, user);
        List<ReadDate> readDates = readDateRepository.findByReadingRecordAndReadingRecord_User(readingRecord, user);
        // 그룹화된 결과 생성
        Map<ReadDateAndIdDto, List<NoteDto>> groupedNotes = groupNotesByReadDate(notes, readDates);

        // 결과를 응답 객체로 변환
        List<NotesByReadDateResponse> response = groupedNotes.entrySet().stream()
                .map(entry -> new NotesByReadDateResponse(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(
                        r -> r.getReadDate().getReadDateId(),
                        Comparator.nullsFirst(Comparator.naturalOrder())
                ))
                .collect(Collectors.toList());

        // 최종 응답 반환
        return response;
    }

    public Map<ReadDateAndIdDto, List<NoteDto>> groupNotesByReadDate(List<Note> notes, List<ReadDate> readDates) {
        // 1. ReadDate를 ReadDateAndIdDto로 변환
        Map<ReadDateAndIdDto, List<NoteDto>> groupedNotes = readDates.stream()
                .map(ReadDateAndIdDto::new)
                .collect(Collectors.toMap(
                        readDateDto -> readDateDto,
                        readDateDto -> new ArrayList<>()
                ));

        // 3. null 키가 없으면 추가
        ReadDateAndIdDto nullKey = new ReadDateAndIdDto(null, null, null, null);
        groupedNotes.putIfAbsent(nullKey , new ArrayList<>());

        // 4. Note를 NoteDto로 변환하고 ReadDateAndIdDto 기준으로 그룹화
        for (Note note : notes) {
            ReadDateAndIdDto key = note.getReadDate() != null
                    ? new ReadDateAndIdDto(note.getReadDate())
                    : nullKey;

            // NoteDto 변환 후 그룹에 추가
            groupedNotes.get(key).add(NoteDto.fromEntity(note));
        }

        return groupedNotes;
    }

    public boolean validateNote(NewNoteRequest request) {
        if(!request.getTitle().isPresent() && !request.getTitle().isPresent()) return false;
        if(request.getPages().isPresent()) {
            if(request.getPages().get() != null) {
                if((request.getPages().get().getStartPage() < 1 || request.getPages().get().getStartPage()>99999)) return false;
                if((request.getPages().get().getEndPage() < 1 || request.getPages().get().getEndPage()>99999)) return false;
            }
        }
        return true;
    }

    public boolean validateUpdateNote(NewNoteRequest request) {
        if(request.getPages().isPresent()) {
            if(request.getPages().get() != null) {
                if((request.getPages().get().getStartPage() < 1 || request.getPages().get().getStartPage()>99999)) return false;
                if((request.getPages().get().getEndPage() < 1 || request.getPages().get().getEndPage()>99999)) return false;
            }
        }
        return true;
    }

    public Long saveNote(Long recordId, NewNoteRequest request, String userSeq) {
        if(!validateNote(request)) throw new GeneralException(Code.BAD_REQUEST);

        ReadingRecord readingRecord = findReadingRecord(recordId, userSeq);
        Note note = Note.createNote(readingRecord);
        updateNote(note, request, readingRecord);
        return noteRepository.save(note).getNoteSeq();
    }

    public void updateNote(Long recordId, Long noteId, NewNoteRequest request, String userSeq) {
        if(!validateUpdateNote(request)) throw new GeneralException(Code.BAD_REQUEST);
        ReadingRecord readingRecord = findReadingRecord(recordId, userSeq);
        Note note = noteRepository.findByNoteSeqAndReadingRecord(noteId, readingRecord)
                .orElseThrow(() -> new GeneralException(Code.NOTE_NOT_FOUND));
        updateNote(note, request, readingRecord);
    }

    public void deleteNote(Long recordId, Long noteId, String userSeq) {
        ReadingRecord readingRecord = findReadingRecord(recordId, userSeq);
        Note note = noteRepository.findByNoteSeqAndReadingRecord(noteId, readingRecord)
                .orElseThrow(() -> new GeneralException(Code.NOTE_NOT_FOUND));
        noteRepository.delete(note);
    }


    //== 사용할 기타 메소드
    public User getUser(String userSeq) {
        User user = userRepository.findByUserSeqAndIsDeleted(Long.valueOf(userSeq), false);
        if(user == null) {
            //탈퇴한 적 있는 회원
            if(userRepository.countByUserSeqAndIsDeleted(Long.valueOf(userSeq), true) > 0) throw new GeneralException(Code.USER_ALREADY_WITHDRAWN);
            else throw new GeneralException(Code.USER_NOT_FOUND);
        }
        return user;
    }

    private ReadingRecord findReadingRecord(Long id, String userSeq){
        User user = getUser(userSeq);
        return readingRecordRepository.findByreadingRecordSeqAndUserOrderByReadingRecordSeq(id, user)
                .orElseThrow(() -> new GeneralException(Code.READING_RECORD_NOT_FOUND));
    }

    private void updateNote(Note note, NewNoteRequest request, ReadingRecord readingRecord) {
        if(request.getTitle().isPresent()) note.updateTitle(request.getTitle().get());
        if(request.getContent().isPresent()) note.updateContent(request.getContent().get());
        if(request.getPage().isPresent()) note.updatePage(request.getPage().get());
        if(request.getPages().isPresent()) {
            if(request.getPages().get() == null) note.updatePages(null);
            else {
                PagesDto dto = request.getPages().get();
                note.updatePages(new Pages(dto.getStartPage(), dto.getEndPage()));
            }
        }
        if(request.getReadDateId().isPresent()) {
            if(request.getReadDateId().get() == null) note.updateReadDate(null);
            else {
                ReadDate readDate = readDateRepository.findByreadDateSeqAndReadingRecord(request.getReadDateId().get(), readingRecord)
                        .orElseThrow(() -> new GeneralException(Code.READ_DATE_NOT_FOUND));
                note.updateReadDate(readDate);
            }
        }
        if(request.getSelectedDate().isPresent()) note.updateSelectedDate(request.getSelectedDate().get());
        if(request.getTags().isPresent()) {
            if(request.getTags().get() == null) throw new GeneralException(Code.BAD_REQUEST, "tags can't null");
            note.updateTags(request.getTags().get());
        }
    }

}
