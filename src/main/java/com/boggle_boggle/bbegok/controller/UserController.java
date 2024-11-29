package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.TermsAgreement;
import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.request.NickNameRequest;
import com.boggle_boggle.bbegok.dto.response.TermsResponse;
import com.boggle_boggle.bbegok.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public DataResponseDto<Boolean> isNicknameAvailable(@RequestParam("nickname") String nickName) {
        return DataResponseDto.of(userService.isNicknameAvailable(nickName));
    }

    //약관조회
    @GetMapping("/terms")
    public DataResponseDto<TermsResponse> getLatestTerms(@AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(userService.getLatestTerms(userDetails.getUsername()));
    }
    
    //약관동의
    @PatchMapping("/terms")
    public DataResponseDto<Null> agreeToTerms(@RequestBody List<TermsAgreement> request, @AuthenticationPrincipal UserDetails userDetails) {
        //약관 유효성 검사
        userService.agreeToTerms(request,userDetails.getUsername());
        return DataResponseDto.empty();
    }
}
