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
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    public List<NotesByReadDateResponse> getNote(Long recordId, String userId) {
        User user = getUser(userId);
        ReadingRecord readingRecord = findReadingRecord(recordId, userId);

        //현재 독서기록에 대한 모든 Note를 찾는다. 이때 readDateSeq별로 그룹바이 해야하고, readDateSeq순서대로 정렬(Null이면 제일 앞으로)
        List<Note> noteList = noteRepository.findByReadingRecordAndReadingRecord_UserOrderByReadDate_ReadDateSeq(readingRecord, user);

        Map<ReadDateAndIdDto, List<NoteDto>> map = new HashMap<>();
        ReadDateAndIdDto nullReadDateAndIDDto = new ReadDateAndIdDto(); // 적절한 기본값 설정
        for (Note note : noteList) {
            ReadDate readDate = note.getReadDate();
            ReadDateAndIdDto key = readDate != null ? new ReadDateAndIdDto(readDate) : nullReadDateAndIDDto;
            map.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(NoteDto.fromEntity(note));
        }

        List<NotesByReadDateResponse> result = new ArrayList<>();
        for (Map.Entry<ReadDateAndIdDto, List<NoteDto>> entry : map.entrySet()) {
            result.add(new NotesByReadDateResponse(entry.getKey(), entry.getValue()));
        }

        // id 기준 오름차순 정렬
        result.sort(Comparator.comparing(
                response -> response.getReadDate().getReadDateId(),
                Comparator.nullsFirst(Comparator.naturalOrder())
        ));

        return result;
    }

    public Long saveNote(Long recordId, NewNoteRequest request, String userId) {
        ReadingRecord readingRecord = findReadingRecord(recordId, userId);
        Note note = Note.createNote(readingRecord);
        updateNote(note, request, readingRecord);
        return noteRepository.save(note).getNoteSeq();
    }

    public void updateNote(Long recordId, Long noteId, NewNoteRequest request, String userId) {
        ReadingRecord readingRecord = findReadingRecord(recordId, userId);
        Note note = noteRepository.findByNoteSeqAndReadingRecord(noteId, readingRecord)
                .orElseThrow(() -> new GeneralException(Code.NOTE_NOT_FOUND));
        updateNote(note, request, readingRecord);
    }

    public void deleteNote(Long recordId, Long noteId, String userId) {
        ReadingRecord readingRecord = findReadingRecord(recordId, userId);
        Note note = noteRepository.findByNoteSeqAndReadingRecord(noteId, readingRecord)
                .orElseThrow(() -> new GeneralException(Code.NOTE_NOT_FOUND));
        noteRepository.delete(note);
    }


    //== 사용할 기타 메소드

    public User getUser(String userId) {
        return userRepository.findByUserId(userId);
    }

    private ReadingRecord findReadingRecord(Long id, String userId){
        User user = getUser(userId);
        return readingRecordRepository.findByreadingRecordSeqAndUser(id, user)
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
        if(request.getTags().isPresent() && request.getTags().get() != null) {
            note.updateTags(request.getTags().get());
        }
    }

}
