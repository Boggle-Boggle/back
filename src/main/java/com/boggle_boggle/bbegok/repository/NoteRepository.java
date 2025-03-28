package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.dto.PagesDto;
import com.boggle_boggle.bbegok.dto.ReadDateDto;
import com.boggle_boggle.bbegok.entity.Note;
import com.boggle_boggle.bbegok.entity.ReadDate;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    Optional<Note> findByNoteSeqAndReadingRecord(Long noteId, ReadingRecord readingRecord);
    List<Note> findByReadingRecordAndReadingRecord_User(ReadingRecord readingRecord, User user);
    List<Note> findByReadingRecord_User(User user);
}
