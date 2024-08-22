package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.response.SearchLogListResponse;
import com.boggle_boggle.bbegok.service.SearchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedisController {

    private final SearchLogService SearchLogService;

    //최근검색어 조회
    @GetMapping
    public DataResponseDto<SearchLogListResponse> getRecentSearchLogs(@AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(SearchLogService.getRecentSearchLogs(userDetails.getUsername()));
    }
}
