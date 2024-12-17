package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.Library;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.ReadingRecordLibraryMapping;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReadingRecordLibraryMappingRepository extends JpaRepository<ReadingRecordLibraryMapping, Long> {
    @Query("""
    SELECT r.readingRecord
    FROM ReadingRecordLibraryMapping r
    WHERE r.library = :library
    AND r.library.user = :user
    """)
    Page<ReadingRecord> findBooksByLibraryAndUser(
            @Param("library") Library library,
            @Param("user") User user,
            Pageable pageable
    );


    @Query("""
    SELECT r.readingRecord
    FROM ReadingRecordLibraryMapping r
    WHERE r.library = :library
    AND r.library.user = :user
    AND LOWER(r.readingRecord.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<ReadingRecord> findBooksByLibraryAndUserAndKeyword( @Param("library") Library library,
                                                             @Param("user") User user,
                                                             @Param("keyword") String keyword,
                                                             Pageable pageable);



    @Query("""
    SELECT r.readingRecord
    FROM ReadingRecordLibraryMapping r
    WHERE r.readingRecord.status = :status
    AND r.library.user = :user
    """)
    Page<ReadingRecord> findBooksByUserAndStatus(
            @Param("status") ReadStatus status,
            @Param("user") User user,
            Pageable pageable
    );

    @Query("""
    SELECT r.readingRecord
    FROM ReadingRecordLibraryMapping r
    WHERE r.readingRecord.status = :status
    AND r.library.user = :user
    AND LOWER(r.readingRecord.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<ReadingRecord> findBooksByUserAndStatusAndKeyword(ReadStatus status, User user, String keyword, Pageable pageable);





}
