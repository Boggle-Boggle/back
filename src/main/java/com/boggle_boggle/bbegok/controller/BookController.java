package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.SearchBookListResponse;
import com.boggle_boggle.bbegok.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public DataResponseDto<SearchBookListResponse> searchBooks(@RequestParam(name = "query") String query,
                                                               @RequestParam(name = "pageNum") int pageNum) {
        return DataResponseDto.of(bookService.getSearchBookList(query, pageNum));
    }

    @GetMapping("/{isbn}")
    public DataResponseDto<BookDetailResponse> getBook(@PathVariable String isbn) {
        return DataResponseDto.of(bookService.getBook(isbn));
    }

}
