package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.TermsAgreement;
import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.request.NickNameRequest;
import com.boggle_boggle.bbegok.dto.response.TermsResponse;
import com.boggle_boggle.bbegok.oauth.service.RevokeService;
import com.boggle_boggle.bbegok.service.UserService;
import com.boggle_boggle.bbegok.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.DEVICE_CODE;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final RevokeService revokeService;
    private final UserService userService;
    @Value("${bbaegok.root-domain}")
    private String domain;

    //회원탈퇴
    @DeleteMapping
    public DataResponseDto<Void> deleteUser(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        revokeService.deleteAccount(userDetails.getUsername());
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN,domain);
        CookieUtil.deleteCookie(request, response, DEVICE_CODE,domain);
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
