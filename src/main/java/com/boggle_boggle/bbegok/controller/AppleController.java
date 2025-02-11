package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.service.AppleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class AppleController {

    private final AppleService appleService;

    @GetMapping("/oauth2/authorization/apple")
    public void loginRequest(HttpServletResponse response,
                             @RequestParam(value = "redirect_uri", required = true) String redirectUri) throws IOException {
        response.sendRedirect(appleService.getAppleLoginUrl(redirectUri));
    }
}
