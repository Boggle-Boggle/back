package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.LibrariesDto;
import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
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
    public DataResponseDto<List<LibrariesDto>> getLibraries(@AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(libraryService.getLibraries(userDetails.getUsername()));
    }

    //새 서재 등록
    @PostMapping("/libraries")
    public DataResponseDto<Void> saveLibrary() {
        return DataResponseDto.empty();
    }

    //특정 서재 삭제
    @DeleteMapping("/libraries")
    public DataResponseDto<Void> deleteLibrary(@RequestParam(name="libraryName") String libraryName) {
        return DataResponseDto.empty();
    }
}
