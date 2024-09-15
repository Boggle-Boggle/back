package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByIsbn(String isbn);
}
