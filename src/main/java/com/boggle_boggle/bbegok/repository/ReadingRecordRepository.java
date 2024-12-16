package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.dto.LibraryBook;
import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.entity.Library;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
    ReadingRecord findByUserAndBook(User user, Book book);

    Optional<ReadingRecord> findByreadingRecordSeqAndUser(Long id, User user);

    @Query(value = """
    SELECT DISTINCT r.readingRecordSeq
    FROM ReadingRecord r
    JOIN r.readDateList rd
    WHERE r.user = :user
    AND r.isBooksVisible = true
    AND (:year IS NULL OR EXTRACT(YEAR FROM rd.endReadDate) = :year)
    AND (:month IS NULL OR EXTRACT(MONTH FROM rd.endReadDate) = :month)
""")
    List<ReadingRecord> findBooksByUserAndReadDate(
            @Param("user") User user,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

}
