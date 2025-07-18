package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.request.CreateCustomBookRequest;
import com.boggle_boggle.bbegok.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/book")
public class CustomBookController {

    private final BookService bookService;

    @PostMapping
    public DataResponseDto<Void> saveCustomBooks(@Valid @RequestBody CreateCustomBookRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        bookService.saveCustomBooks(request, userDetails.getUsername());
        return DataResponseDto.empty();
    }

    @PutMapping("/{id}")
    public DataResponseDto<Void> updateCustomBooks(@PathVariable(name = "id") Long bookId,
                                                   @Valid @RequestBody CreateCustomBookRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        bookService.updateCustomBooks(bookId, request, userDetails.getUsername());
        return DataResponseDto.empty();
    }

}
