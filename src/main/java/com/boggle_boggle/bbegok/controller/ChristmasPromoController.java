package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.response.ChristmasPromoResponse;
import com.boggle_boggle.bbegok.service.ChristmasPromoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/promo")
public class ChristmasPromoController {

    private final ChristmasPromoService christmasPromoService;

    @GetMapping("/christmas")
    public DataResponseDto<ChristmasPromoResponse> getChristmasPromo(
            @AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(christmasPromoService.getChristmasPromo(userDetails.getUsername()));
    }
}
