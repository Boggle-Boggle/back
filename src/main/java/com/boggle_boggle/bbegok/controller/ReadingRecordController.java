package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.ReadDateAndIdDto;
import com.boggle_boggle.bbegok.dto.ReadDateIndexDto;
import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.request.NewReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.request.UpdateReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.response.*;
import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.service.BookService;
import com.boggle_boggle.bbegok.service.ReadingRecordService;
import jakarta.validation.Valid;
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
    @GetMapping("/{id}")
    public DataResponseDto<ReadingRecordResponse> getReadingRecord(@PathVariable(name = "id") Long id,
                                                                   @AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(readingRecordService.getReadingRecord(id, userDetails.getUsername()));
    }

    //독서기록 있는지 조회
    @GetMapping("/isbn/{isbn}")
    public DataResponseDto<Long> getReadingRecordId(@PathVariable(name = "isbn") String isbn,
                                                                   @AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(readingRecordService.getReadingRecordId(isbn, userDetails.getUsername()));
    }

    //새로운 독서기록 등록
    @PostMapping
    public DataResponseDto<ReadingRecordIdResponse> save(
            @Valid @RequestBody NewReadingRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(readingRecordService.save(request, userDetails.getUsername()));
    }

    //독서기록 수정창 조회
    @GetMapping("/{id}/edit")
    public DataResponseDto<EditReadingRecordResponse> getUpdateReadingRecord(@PathVariable(name = "id") Long readingRecordId,
                                                                             @AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(readingRecordService.getEditReadingRecord(readingRecordId, userDetails.getUsername()));
    }

    //독서기록 수정
    @PatchMapping("/{id}")
    public DataResponseDto<Void> updateReadingRecord(@PathVariable(name = "id") Long readingRecordId,
                                                        @RequestBody UpdateReadingRecordRequest request,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        readingRecordService.updateReadingRecord(readingRecordId, request, userDetails.getUsername());
        return DataResponseDto.empty();
    }

    //독서 기록 삭제
    @DeleteMapping("/{id}")
    public DataResponseDto<Void> deleteReadingRecord(@PathVariable(name="id") Long readingRecordId,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        readingRecordService.deleteReadingRecord(readingRecordId, userDetails.getUsername());
        return DataResponseDto.empty();
    }

    //독서기록의 회독정보들만 조회
    @GetMapping("/{id}/read-dates")
    public DataResponseDto<List<ReadDateIndexDto>> getReadDates(@PathVariable(name = "id") Long readingRecordId,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(readingRecordService.getReadDates(readingRecordId, userDetails.getUsername()));
    }
}
