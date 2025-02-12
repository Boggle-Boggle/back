package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
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
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @GetMapping("/oauth2/apple")
    public void loginRequest(HttpServletResponse response,
                             @RequestParam(value = "redirect_uri", required = true) String redirectUri) throws IOException {
        response.sendRedirect(appleService.getAppleLoginUrl(redirectUri));
    }

    @PostMapping("/login/oauth2/code/apple")
    public void callback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = appleService.process(request.getParameter("code"));

        if(user != null) {
            String accessToken = appleService.loginSuccess(request, response, user);
            redirectStrategy.sendRedirect(request, response, appleService.determineSuccessRedirectUrl(accessToken, request.getParameter("state")));
        }
        else throw new GeneralException();
    }
}
