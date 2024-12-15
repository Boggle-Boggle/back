package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.request.NickNameRequest;
import com.boggle_boggle.bbegok.dto.request.SortingRequest;
import com.boggle_boggle.bbegok.enums.SortingType;
import com.boggle_boggle.bbegok.service.UserService;
import com.boggle_boggle.bbegok.service.UserSettingsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/settings")
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    @GetMapping("/sorting")
    public DataResponseDto<String> getSortingType(@AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(userSettingsService.getSortingType(userDetails.getUsername()));
    }

    @PatchMapping("/sorting")
    public DataResponseDto<Void> updateSortingType(@AuthenticationPrincipal UserDetails userDetails,
                                                   @Valid @RequestBody SortingRequest request) {
        userSettingsService.updateSortingType(userDetails.getUsername(), request.getSortingType());
        return DataResponseDto.empty();
    }


}
