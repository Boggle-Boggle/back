package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.dto.LibraryBook;
import com.boggle_boggle.bbegok.dto.ReadingRecordAndDateDTO;
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
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
    ReadingRecord findByUserAndBook(User user, Book book);

    Optional<ReadingRecord> findByreadingRecordSeqAndUserOrderByReadingRecordSeq(Long id, User user);

    @Query(value = """
    SELECT COUNT(DISTINCT r)
    FROM ReadingRecord r
    JOIN r.readDateList rd
    WHERE r.user = :user
      AND rd.status = :status
    """)
    int findTotalyReadingCnt(
            @Param("user") User user,
            @Param("status") ReadStatus status
    );

    @Query(value = """
    SELECT COUNT(DISTINCT r)
    FROM ReadingRecord r
    JOIN r.readDateList rd
    WHERE r.user = :user
      AND rd.status = :status
      AND EXTRACT(YEAR FROM rd.endReadDate) = EXTRACT(YEAR FROM CURRENT_DATE)
      AND EXTRACT(MONTH FROM rd.endReadDate) = EXTRACT(MONTH FROM CURRENT_DATE)
    """)
    int findMonthlyReadingCnt(
                    @Param("user") User user,
                    @Param("status") ReadStatus status
            );


    @Query(value = """
    SELECT new com.boggle_boggle.bbegok.dto.ReadingRecordAndDateDTO(r, rd)
    FROM ReadingRecord r
    JOIN r.readDateList rd
    WHERE r.user = :user
    AND r.isBooksVisible = true
    AND (:year IS NULL OR EXTRACT(YEAR FROM rd.endReadDate) = :year)
    AND (:month IS NULL OR EXTRACT(MONTH FROM rd.endReadDate) = :month)
    AND rd.status = :status
    ORDER BY rd.endReadDate ASC, r.readingRecordSeq
    """)
    List<ReadingRecordAndDateDTO> findBooksByUserAndReadDate(
            @Param("user") User user,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("status") ReadStatus status

    );


    @Query("""
    SELECT DISTINCT r
    FROM ReadingRecord r
    JOIN r.readDateList rd
    WHERE rd.status = :status
    AND r.user = :user
    """)
    Page<ReadingRecord> findBooksByUserAndStatus(
            @Param("status") ReadStatus status,
            @Param("user") User user,
            Pageable pageable
    );

    @Query("""
    SELECT new com.boggle_boggle.bbegok.dto.RecordByStatusDto(rd.status, COUNT(DISTINCT r))
    FROM ReadingRecord r
    JOIN r.readDateList rd
    WHERE r.user = :user
    GROUP BY rd.status
    """)
    List<RecordByStatusDto> countReadingRecordsByStatus(@Param("user") User user);

    @Query("""
    SELECT new com.boggle_boggle.bbegok.dto.RecordByStatusDto(COUNT(DISTINCT r))
    FROM ReadingRecord r
    WHERE r.user = :user
    """)
    RecordByStatusDto countReadingRecordsByStatusIsAll(@Param("user") User user);


    @Query("""
    SELECT DISTINCT r
    FROM ReadingRecord r
    JOIN r.readDateList rd
    WHERE rd.status = :status
    AND r.user = :user
    AND LOWER(r.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<ReadingRecord> findBooksByUserAndStatusAndKeyword(ReadStatus status, User user, String keyword, Pageable pageable);



    @Query("""
    SELECT DISTINCT r
    FROM ReadingRecord r
    WHERE r.user = :user
    """)
    Page<ReadingRecord> findBooksWithReadingRecordIdByUser(
            @Param("user") User user,
            Pageable pageable
    );

    @Query("""
    SELECT DISTINCT r
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
