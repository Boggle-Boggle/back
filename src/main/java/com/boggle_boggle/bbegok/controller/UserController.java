package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.request.NickNameRequest;
import com.boggle_boggle.bbegok.dto.response.SearchBookListResponse;
import com.boggle_boggle.bbegok.dto.response.TermsResponse;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import com.boggle_boggle.bbegok.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    //닉네임 수정
    @PatchMapping("/nickname")
    public DataResponseDto<Null> updateNickname(@AuthenticationPrincipal UserDetails userDetails,
                                                @Valid @RequestBody NickNameRequest request) {
        userService.updateNicName(userDetails.getUsername(), request.getNickname());
        return DataResponseDto.empty();
    }

    //닉네임 중복확인
    @GetMapping("/nickname")
    public DataResponseDto<Boolean> isNicknameAvailable(@Valid @RequestBody NickNameRequest request) {
        return DataResponseDto.of(userService.isNicknameAvailable(request.getNickname()));
    }

    //약관조회
    @GetMapping("/terms")
    public DataResponseDto<TermsResponse> getLatestTerms() {
        return DataResponseDto.of(userService.getLatestTerms());
    }
    
    //약관동의
    @PatchMapping("/terms")
    public DataResponseDto<Null> agreeToTerms(@AuthenticationPrincipal UserDetails userDetails) {
        userService.agreeToTerms(userDetails.getUsername());
        return DataResponseDto.empty();
    }
}
