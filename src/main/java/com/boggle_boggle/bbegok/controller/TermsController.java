package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.TermsAgreement;
import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.response.TermsResponse;
import com.boggle_boggle.bbegok.service.TermsService;
import com.boggle_boggle.bbegok.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/terms")
@RequiredArgsConstructor
public class TermsController {
    private final UserService userService;
    private final TermsService termsService;

    //약관조회
    @GetMapping
    public DataResponseDto<TermsResponse> getLatestTerms() {
        return DataResponseDto.of(termsService.getLatestTerms());
    }

    /** 약관동의
    @PutMapping
    public DataResponseDto<Void> agreeToTerms(@RequestBody @Valid List<TermsAgreement> request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        userService.agreeToTerms(request,userDetails.getUsername());
        return DataResponseDto.empty();
    }
  **/
}
