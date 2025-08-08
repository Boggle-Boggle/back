package com.boggle_boggle.bbegok.api;

import com.boggle_boggle.bbegok.AbstractRestDocsTests;
import com.boggle_boggle.bbegok.RestDocsConfiguration;
import com.boggle_boggle.bbegok.config.properties.CorsProperties;
import com.boggle_boggle.bbegok.controller.OAuth2AuthController;
import com.boggle_boggle.bbegok.dto.OAuthLoginResponse;
import com.boggle_boggle.bbegok.dto.TermsAgreement;
import com.boggle_boggle.bbegok.dto.TokenDto;
import com.boggle_boggle.bbegok.dto.request.SignupRequest;
import com.boggle_boggle.bbegok.enums.SignStatus;
import com.boggle_boggle.bbegok.oauth.client.OAuth2RedirectUriBuilder;
import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.service.OAuth2LoginService;
import com.boggle_boggle.bbegok.service.QueryService;
import com.boggle_boggle.bbegok.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OAuth2AuthController.class)
@Import(RestDocsConfiguration.class)
class OAuth2AuthControllerTest extends AbstractRestDocsTests {

    @MockBean
    private OAuth2LoginService oauth2LoginService;
    @MockBean
    private QueryService queryService;
    @MockBean
    private OAuth2RedirectUriBuilder oAuth2RedirectUriBuilder;
    @MockBean
    private UserService userService;
    @MockBean
    private CorsProperties corsProperties;

    @Test
    @WithMockUser
    void refreshDocs() throws Exception {
        // given
        String mockRefreshToken = "mock-refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        given(oauth2LoginService.refresh(mockRefreshToken))
                .willReturn(new TokenDto(newAccessToken, newRefreshToken, true));

        // when + then
        mockMvc.perform(get("/auth/refresh")
                        .cookie(new Cookie("refresh_token", mockRefreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value(newAccessToken))
                .andDo(document("auth/refresh",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        relaxedResponseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("새로 발급된 액세스 토큰")
                        )
                ));
    }

    @Test
    @WithMockUser
    void oauth2AuthorizeDocs() throws Exception {
        // given
        given(corsProperties.getAllowedOrigins()).willReturn(Collections.singletonList("https://front-uri"));
        // OAuth2RedirectUriBuilder가 리턴할 값 mocking
        given(oAuth2RedirectUriBuilder.buildRedirectUri(any(), anyString()))
                .willReturn("https://authorization-uri");

        this.mockMvc.perform(get("/auth/oauth2/authorize")
                        .param("provider", "kakao")
                        .param("redirect", "https://front-uri"))
                .andExpect(status().is3xxRedirection()) // 302
                .andExpect(redirectedUrl("https://authorization-uri"))
                .andDo(document("auth/oauth2-authorize",
                        queryParameters(
                                parameterWithName("provider")
                                        .description("소셜 로그인 제공자 (kakao, google, apple)"),
                                parameterWithName("redirect")
                                        .description("프론트엔드 URI")
                        )
                ));
    }


    @Test
    @WithMockUser
    void oauth2CallbackDocs() throws Exception {
        given(corsProperties.getAllowedOrigins())
                .willReturn(Collections.singletonList("https://front-uri"));

        OAuthLoginResponse dummyResponse = OAuthLoginResponse.builder()
                .status(SignStatus.EXISTING_USER)
                .refreshToken("mock-refresh-token")
                .deviceCode("mock-device-id")
                .build();

        given(oauth2LoginService.processOAuth2Callback(eq(ProviderType.KAKAO), eq("sample-code"), eq("optional-state")))
                .willReturn(dummyResponse);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("oauth2_state", "optional-state");
        session.setAttribute("redirect_front", "https://front-uri");

        mockMvc.perform(get("/auth/oauth2/callback/{provider}", "kakao")
                        .param("code", "sample-code")
                        .param("state", "optional-state")
                        .session(session))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://front-uri/oauth?status=EXISTING_USER"))
                .andDo(document("auth/oauth2-callback",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("provider").description("OAuth2 로그인 제공자 (kakao, google, apple)")
                        ),
                        queryParameters(
                                parameterWithName("code").description("OAuth2 인가 코드"),
                                parameterWithName("state").description("CSRF 방지를 위한 state 값")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("리디렉션될 프론트엔드 URL")
                        )
                ));
    }


    @Test
    @WithMockUser
    void signupDocs() throws Exception {
        // given
        SignupRequest signupRequest = new SignupRequest();
        ReflectionTestUtils.setField(signupRequest, "nickname", "초코우유");
        ReflectionTestUtils.setField(signupRequest, "agreements", List.of(
                TermsAgreement.of(1L, true),
                TermsAgreement.of(2L, true)
        ));

        OAuthLoginResponse mockResponse = OAuthLoginResponse.builder()
                .status(SignStatus.EXISTING_USER)
                .refreshToken("mock-refresh-token")
                .deviceCode("mock-device-code")
                .build();

        given(userService.signup(anyLong(), anyString(), any()))
                .willReturn(mockResponse);

        doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            Cookie cookie = new Cookie("refresh_token", "fake-token");
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            response.addCookie(cookie);
            return null;
        }).when(queryService).setLoginCookie(any(), any(), any());

        mockMvc.perform(post("/auth/signup")
                        .cookie(new Cookie("pre_signup_id", "1"))
                        .contentType("application/json")
                        .content(toJson(signupRequest)))
                .andExpect(status().isOk())
                .andDo(document("auth/oauth2-signup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").description("사용자 닉네임 (최대 15자)"),
                                fieldWithPath("agreements[].id").description("동의한 약관 ID"),
                                fieldWithPath("agreements[].isAgree").description("해당 약관 동의 여부")
                        ),
                        responseHeaders(
                                headerWithName("Set-Cookie").description("응답 쿠키\n" +
                                        "- `refresh_token`: 리프레시 토큰, HttpOnly, 30일\n" +
                                        "- `device_code`: 디바이스 식별자, HttpOnly, 30일")
                        )
                ));
    }


}
