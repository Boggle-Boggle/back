package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.ReadDate;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadDateRepository extends JpaRepository<ReadDate, Long> {
    Optional<ReadDate> findByreadDateSeqAndReadingRecord(Long id, ReadingRecord readingRecord);
    List<ReadDate> findByReadingRecordAndReadingRecord_User(ReadingRecord readingRecord, User user);
    List<ReadDate> findByReadingRecordOrderByReadDateSeq(ReadingRecord readingRecord);
}
