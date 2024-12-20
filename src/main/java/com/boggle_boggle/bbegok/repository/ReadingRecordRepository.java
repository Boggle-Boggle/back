package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.dto.LibraryBook;
import com.boggle_boggle.bbegok.dto.RecordByStatusDto;
import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.entity.Library;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
    ReadingRecord findByUserAndBook(User user, Book book);

    Optional<ReadingRecord> findByreadingRecordSeqAndUser(Long id, User user);

    @Query(value = """
    SELECT DISTINCT r
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


    @Query("""
    SELECT r
    FROM ReadingRecord r
    WHERE r.status = :status
    AND r.user = :user
    """)
    Page<ReadingRecord> findBooksByUserAndStatus(
            @Param("status") ReadStatus status,
            @Param("user") User user,
            Pageable pageable
    );

    @Query("""
    SELECT new com.boggle_boggle.bbegok.dto.RecordByStatusDto(r.status, COUNT(r))
    FROM ReadingRecord r
    WHERE r.user = :user
    GROUP BY r.status
    """)
    List<RecordByStatusDto> countReadingRecordsByStatus(@Param("user") User user);


    @Query("""
    SELECT r
    FROM ReadingRecord r
    WHERE r.status = :status
    AND r.user = :user
    AND LOWER(r.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<ReadingRecord> findBooksByUserAndStatusAndKeyword(ReadStatus status, User user, String keyword, Pageable pageable);



    @Query("""
    SELECT r
    FROM ReadingRecord r
    WHERE r.user = :user
    """)
    Page<ReadingRecord> findBooksWithReadingRecordIdByUser(
            @Param("user") User user,
            Pageable pageable
    );

    @Query("""
    SELECT r
    FROM ReadingRecord r
    WHERE r.user = :user
    AND LOWER(r.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<ReadingRecord> findBooksWithReadingRecordIdByUserAndKeyword(
            @Param("user") User user,
            String keyword,
            Pageable pageable
    );

}
