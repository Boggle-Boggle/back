package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.ReadDate;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ReadDateRepository extends JpaRepository<ReadDate, Long> {
    Optional<ReadDate> findByreadDateSeqAndReadingRecord(Long id, ReadingRecord readingRecord);
    List<ReadDate> findByReadingRecordOrderByReadDateSeq(ReadingRecord readingRecord);
    List<ReadDate> findByReadingRecordAndReadingRecord_User(ReadingRecord readingRecord, User user);
}
