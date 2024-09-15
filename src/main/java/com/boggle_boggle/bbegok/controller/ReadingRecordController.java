package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.request.NewReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.request.UpdateReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.LibraryResponse;
import com.boggle_boggle.bbegok.dto.response.ReadingRecordResponse;
import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.service.BookService;
import com.boggle_boggle.bbegok.service.ReadingRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reading-record")
public class ReadingRecordController {
    private final ReadingRecordService readingRecordService;

    //독서기록 조회
    @GetMapping("/{isbn}")
    public DataResponseDto<ReadingRecordResponse> getReadingRecord(@PathVariable(name = "isbn") String isbn,
                                                                   @AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(readingRecordService.getReadingRecord(isbn, userDetails.getUsername()));
    }

    //새로운 독서기록 등록
    @PostMapping
    public DataResponseDto<Void> saveReadingRecord(
            @RequestBody NewReadingRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        readingRecordService.saveReadingRecord(request, userDetails.getUsername());
        return DataResponseDto.empty();
    }

    //독서기록 수정
    @PatchMapping
    public DataResponseDto<Void> updateReadingRecord(
            @RequestBody UpdateReadingRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        readingRecordService.updateReadingRecord(request, userDetails.getUsername());
        return DataResponseDto.empty();
    }

    //독서노트
}
