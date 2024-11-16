package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.Note;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    Optional<Note> findByNoteSeqAndReadingRecord(Long noteId, ReadingRecord readingRecord);
}
