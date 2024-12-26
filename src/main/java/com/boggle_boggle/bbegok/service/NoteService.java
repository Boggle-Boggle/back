package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.PagesDto;
import com.boggle_boggle.bbegok.dto.request.NewNoteRequest;
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

import java.util.Optional;

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


    public void saveNote(Long recordId, NewNoteRequest request, String userId) {
        ReadingRecord readingRecord = findReadingRecord(recordId, userId);
        Note note = Note.createNote(readingRecord);
        updateNote(note, request, readingRecord);
        noteRepository.save(note);
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
        if(request.getTags().isPresent()) note.updateTags(request.getTags().get());
    }

}
