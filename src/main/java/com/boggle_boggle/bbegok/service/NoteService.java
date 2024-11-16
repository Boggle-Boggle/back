package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.request.NewNoteRequest;
import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.entity.Library;
import com.boggle_boggle.bbegok.entity.Note;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.*;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        noteRepository.save(Note.createNote(readingRecord, request.getTitle(), request.getContent()));
    }

    public void updateNote(Long recordId, Long noteId, NewNoteRequest request, String userId) {
        ReadingRecord readingRecord = findReadingRecord(recordId, userId);
        Note note = noteRepository.findByNoteSeqAndReadingRecord(noteId, readingRecord)
                .orElseThrow(() -> new GeneralException(Code.NOTE_NOT_FOUND));
        note.updateNote(request.getTitle(), request.getContent());
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
}
