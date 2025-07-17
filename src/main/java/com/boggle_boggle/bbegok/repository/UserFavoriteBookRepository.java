package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.UserFavoriteBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFavoriteBookRepository extends JpaRepository<UserFavoriteBook, Long> {
    @Query("""
        select b.book.isbn
        from UserFavoriteBook b
        where b.user.userSeq = :userSeq
        and b.book.isbn in :isbnList
    """)
    List<String> findFavoriteIsbns(@Param("userSeq") Long userSeq, @Param("isbnList") List<String> isbnList);

    boolean existsByUser_UserSeqAndBook_Isbn(Long userSeq, String isbn);
}
