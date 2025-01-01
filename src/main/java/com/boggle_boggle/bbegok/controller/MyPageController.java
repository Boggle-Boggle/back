package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.request.NickNameRequest;
import com.boggle_boggle.bbegok.dto.response.MyPageResponse;
import com.boggle_boggle.bbegok.service.MyPageService;
import com.boggle_boggle.bbegok.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {
    private final MyPageService myPageService;

    //마이페이지 조회
    @GetMapping("")
    public DataResponseDto<MyPageResponse> getMyPage(@AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(myPageService.getMyPage(userDetails.getUsername()));
    }
}
