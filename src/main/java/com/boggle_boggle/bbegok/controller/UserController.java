package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.OAuthLoginResponse;
import com.boggle_boggle.bbegok.dto.TermsAgreement;
import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.request.NickNameRequest;
import com.boggle_boggle.bbegok.dto.request.SignupRequest;
import com.boggle_boggle.bbegok.dto.request.WithdrawReasonRequest;
import com.boggle_boggle.bbegok.dto.response.TermsResponse;
import com.boggle_boggle.bbegok.enums.SignStatus;
import com.boggle_boggle.bbegok.oauth.service.RevokeService;
import com.boggle_boggle.bbegok.service.QueryService;
import com.boggle_boggle.bbegok.service.UserService;
import com.boggle_boggle.bbegok.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    private final QueryService queryService;
    private final UserService userService;


    @DeleteMapping
    public DataResponseDto<Void> deleteUser(HttpServletRequest request, HttpServletResponse response,
                                            @Valid @RequestBody WithdrawReasonRequest withdrawReasonRequest,
                                            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        //계정 삭제 및 revoke 처리
        revokeService.deleteAccount(userDetails.getUsername(), withdrawReasonRequest);
        //쿠키삭제
        queryService.clearAllCookie(request, response);
        return DataResponseDto.empty();
    }
/*
    //닉네임 수정
    @PatchMapping("/nickname")
    public DataResponseDto<Void> updateNickname(@AuthenticationPrincipal UserDetails userDetails,
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

 */
}
