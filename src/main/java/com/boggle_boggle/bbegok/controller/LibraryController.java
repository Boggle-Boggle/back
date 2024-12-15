package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.LibrariesDto;
import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.request.LibraryRequest;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.BookShelfResponse;
import com.boggle_boggle.bbegok.dto.response.LibraryBookListResponse;
import com.boggle_boggle.bbegok.dto.response.LibraryResponse;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.service.LibraryService;
import com.boggle_boggle.bbegok.service.SearchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    //서재 목록 조회
    @GetMapping("/libraries")
    public DataResponseDto<List<LibraryResponse>> getLibraries(@AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(libraryService.getLibraries(userDetails.getUsername()));
    }

    //새 서재 등록
    @PostMapping("/libraries")
    public DataResponseDto<Void> saveLibrary(@RequestBody LibraryRequest request,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        libraryService.saveNewLibrary(request, userDetails.getUsername());
        return DataResponseDto.empty();
    }

    //특정 서재 삭제
    @DeleteMapping("/libraries")
    public DataResponseDto<Void> deleteLibrary(@RequestParam(name="libraryId") Long libraryId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        libraryService.deleteLibrary(libraryId, userDetails.getUsername());
        return DataResponseDto.empty();
    }

    //서재의 독서기록을 조회
    @GetMapping("/library")
    public DataResponseDto<LibraryBookListResponse>searchLibrary(@RequestParam(required = false) Long libraryId,
                                                                 @RequestParam(required = false) ReadStatus status,
                                                                 @RequestParam(defaultValue = "30") int pageSize,
                                                                 @RequestParam(required = false) String keyword,
                                                                 @RequestParam(defaultValue = "1") int pageNum,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        if (libraryId != null) return DataResponseDto.of(libraryService.findByLibraryId(libraryId, pageNum, userDetails.getUsername(), pageSize, keyword));
        else if (status != null) return DataResponseDto.of(libraryService.findByStatus(status, pageNum, userDetails.getUsername(), pageSize, keyword));
        else return DataResponseDto.of(libraryService.findAll(pageNum, userDetails.getUsername(), pageSize, keyword));
    }

    //책장을 조회
    @GetMapping("/bookshelf")
    public DataResponseDto<BookShelfResponse>findBookshelfByEndDate(@RequestParam(required = false) Integer year,
                                                                    @RequestParam(required = false) Integer month,
                                                                    @AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(libraryService.findBookshelfByEndDate(year, month, userDetails.getUsername()));
    }
}
