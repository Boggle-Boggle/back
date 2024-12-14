package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.SearchLogs;
import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.request.DeleteRecentSearchRequest;
import com.boggle_boggle.bbegok.dto.request.RecentSearchRequest;
import com.boggle_boggle.bbegok.dto.response.SearchLogListResponse;
import com.boggle_boggle.bbegok.service.SearchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recent-searches")
public class RedisController {

    private final SearchLogService SearchLogService;

    //최근검색어 조회
    @GetMapping
    public DataResponseDto<List<String>> getRecentSearchLogs(@AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(SearchLogService.getRecentSearchLogs(userDetails.getUsername()));
    }

    //최근검색어 저장
    @PostMapping
    public DataResponseDto<Void> saveRecentSearchLogs(@AuthenticationPrincipal UserDetails userDetails,
                                                        @RequestBody RecentSearchRequest request) {
        SearchLogService.saveRecentSearchLogs(userDetails.getUsername(), request.getKeyword());
        return DataResponseDto.empty();
    }

    //최근검색어 삭제
    @DeleteMapping
    public DataResponseDto<Void> deleteRecentSearchLogs(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody DeleteRecentSearchRequest request) {
        SearchLogService.deleteRecentSearchLog(userDetails.getUsername(), request.getKeyword());
        return DataResponseDto.empty();
    }

    //최근검색어 전체삭제
    @DeleteMapping("/all")
    public DataResponseDto<Void> deleteAllRecentSearchLog(@AuthenticationPrincipal UserDetails userDetails) {
        SearchLogService.deleteAllRecentSearchLog(userDetails.getUsername());
        return DataResponseDto.empty();
    }
}
