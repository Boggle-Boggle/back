package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.service.RevokeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class RevokeController {
    private final RevokeService revokeService;

    @DeleteMapping("/oauth2/revoke")
    public DataResponseDto<Void> revokeAccount(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
        revokeService.deleteAccount(userDetails.getUsername());
        return DataResponseDto.empty();
    }
}