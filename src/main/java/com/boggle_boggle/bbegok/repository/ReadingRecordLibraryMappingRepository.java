package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.dto.LibraryBook;
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
    SELECT DISTINCT new com.boggle_boggle.bbegok.dto.LibraryBook(r.readingRecord.readingRecordSeq, r.readingRecord.book.title, r.readingRecord.book.page)
    FROM ReadingRecordLibraryMapping r
    WHERE r.library = :library
    AND r.library.user = :user
    """)
    Page<LibraryBook> findBooksByLibraryAndUser(
            @Param("library") Library library,
            @Param("user") User user,
            Pageable pageable
    );

    @Query("""
    SELECT DISTINCT new com.boggle_boggle.bbegok.dto.LibraryBook(r.readingRecord.readingRecordSeq, r.readingRecord.book.title, r.readingRecord.book.page)
    FROM ReadingRecordLibraryMapping r
    WHERE r.readingRecord.status = :status
    AND r.library.user = :user
    """)
    Page<LibraryBook> findBooksByUserAndStatus(
            @Param("status") ReadStatus status,
            @Param("user") User user,
            Pageable pageable
    );


    @Query("""
    SELECT DISTINCT new com.boggle_boggle.bbegok.dto.LibraryBook(r.readingRecord.readingRecordSeq, r.readingRecord.book.title, r.readingRecord.book.page)
    FROM ReadingRecordLibraryMapping r
    WHERE r.readingRecord.user = :user
    """)
    Page<LibraryBook> findBooksWithReadingRecordIdByUser(
            @Param("user") User user,
            Pageable pageable
    );
}
