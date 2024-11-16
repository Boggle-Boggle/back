package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.entity.Library;
import com.boggle_boggle.bbegok.entity.ReadingRecordLibraryMapping;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public interface ReadingRecordLibraryMappingRepository extends JpaRepository<ReadingRecordLibraryMapping, Long> {
    @Query("""
    SELECT r.readingRecord.book
    FROM ReadingRecordLibraryMapping r
    WHERE r.library = :library
    AND r.library.user = :user
    ORDER BY r.readingRecord.crudDate.createAt ASC
    """)
    Page<Book> findBooksByLibraryAndUser(
            @Param("library") Library library,
            @Param("user") User user,
            Pageable pageable
    );

    @Query("""
    SELECT r.readingRecord.book
    FROM ReadingRecordLibraryMapping r
    WHERE r.readingRecord.status = :status
    AND r.library.user = :user
    ORDER BY r.readingRecord.crudDate.createAt ASC
    """)
    Page<Book> findBooksByUserAndStatus(
            @Param("status") ReadStatus status,
            @Param("user") User user,
            Pageable pageable
    );


    @Query("""
    SELECT r.readingRecord.book
    FROM ReadingRecordLibraryMapping r
    WHERE r.readingRecord.user = :user
    """)
    Page<Book> findBooksByUser(
            @Param("user") User user,
            Pageable pageable
    );
}
