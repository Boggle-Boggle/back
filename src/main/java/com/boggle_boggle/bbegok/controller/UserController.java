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

    //회원탈퇴
    @DeleteMapping
    public DataResponseDto<Null> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUser(userDetails.getUsername());
        return DataResponseDto.empty();
    }

    //닉네임 수정
    @PatchMapping("/nickname")
    public DataResponseDto<Null> updateNickname(@AuthenticationPrincipal UserDetails userDetails,
                                                @Valid @RequestBody NickNameRequest request) {
        userService.updateNicName(userDetails.getUsername(), request.getNickname());
        return DataResponseDto.empty();
    }

    //닉네임 중복확인
    @GetMapping("/nickname")
    public DataResponseDto<Boolean> isNicknameAvailable(@AuthenticationPrincipal UserDetails userDetails,
                                                        @RequestParam("nickname") String nickName) {
        return DataResponseDto.of(userService.isNicknameAvailable(userDetails.getUsername(), nickName));
    }

    //권한확인
    @GetMapping("/authorization")
    public DataResponseDto<String> getAuthorization(@AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(userService.getAuthorization(userDetails.getUsername()));
    }

    //약관조회
    @GetMapping("/terms")
    public DataResponseDto<TermsResponse> getLatestTerms(@AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(userService.getLatestTerms(userDetails.getUsername()));
    }
    
    //약관동의
    @PutMapping("/terms")
    public DataResponseDto<Null> agreeToTerms(@RequestBody @Valid List<TermsAgreement> request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        userService.agreeToTerms(request,userDetails.getUsername());
        return DataResponseDto.empty();
    }
}
