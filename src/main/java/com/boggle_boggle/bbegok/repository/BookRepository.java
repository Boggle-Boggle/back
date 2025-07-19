package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbnAndIsCustomFalse(String isbn);

    Optional<Book> findByBookSeqAndCreatedByUser(Long bookSeq, User user);

    Optional<Book> findByIsbn(String isbn);
}
