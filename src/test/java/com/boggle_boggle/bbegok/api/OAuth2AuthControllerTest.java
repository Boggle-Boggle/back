package com.boggle_boggle.bbegok.api;

import com.boggle_boggle.bbegok.AbstractRestDocsTests;
import com.boggle_boggle.bbegok.RestDocsConfiguration;
import com.boggle_boggle.bbegok.controller.OAuth2AuthController;
import com.boggle_boggle.bbegok.dto.OAuthLoginResponse;
import com.boggle_boggle.bbegok.dto.TermsAgreement;
import com.boggle_boggle.bbegok.dto.request.SignupRequest;
import com.boggle_boggle.bbegok.oauth.client.OAuth2RedirectUriBuilder;
import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.service.OAuth2LoginService;
import com.boggle_boggle.bbegok.service.QueryService;
import com.boggle_boggle.bbegok.service.UserService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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


    @Test
    @WithMockUser
    void oauth2AuthorizeDocs() throws Exception {
        // given
        String dummyRedirectUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=example&redirect_uri=http://example.com";

        // OAuth2RedirectUriBuilder가 리턴할 값 mocking
        given(oAuth2RedirectUriBuilder.buildRedirectUri(any(), anyString()))
                .willReturn(dummyRedirectUrl);

        // 수동 주입 (이 테스트에선 controller 생성 필요 없이 mockBean만으로 처리하면 됨)
        this.mockMvc.perform(get("/auth/oauth2/authorize")
                        .param("provider", "kakao"))
                .andExpect(status().isOk())
                .andDo(document("auth/oauth2-authorize",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("provider")
                                        .description("소셜 로그인 제공자 (kakao, google, apple)")
                        ),
                        relaxedResponseFields( // ← 바뀐 부분
                                beneathPath("data").withSubsectionId("data"), // ← 바뀐 부분
                                fieldWithPath("redirectUrl")
                                        .description("Redirect할 인증서버 URI")
                                        .type(JsonFieldType.STRING)
                        )
                ));
    }


    @Test
    @WithMockUser
    void oauth2CallbackDocs() throws Exception {
        // 기존회원 응답 샘플
        OAuthLoginResponse existingUserResponse = OAuthLoginResponse.existingUser(
                "access-token-sample",
                null,  // refreshToken 응답에서 제외됨
                null   // deviceCode 응답에서 제외됨
        );

        given(oauth2LoginService.processOAuth2Callback(any(), anyString(), any()))
                .willReturn(existingUserResponse);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("oauth2_state", "optional-state");

        mockMvc.perform(get("/auth/oauth2/callback/{provider}", "kakao")
                        .param("code", "sample-code")
                        .param("state", "optional-state")
                        .session(session))
                .andExpect(status().isOk())
                .andDo(document("auth/oauth2-callback",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("provider").description("소셜 로그인 제공자 (KAKAO, GOOGLE, APPLE)")
                        ),
                        queryParameters(
                                parameterWithName("code").description("OAuth2 인가코드"),
                                parameterWithName("state").optional().description("상태 값 (선택, CSRF 방지용)")
                        ),
                        relaxedResponseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("status")
                                        .description("""
                                        EXISTING_USER:기존회원
                                        SIGNUP_REQUIRED:신규회원
                                        """)
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("accessToken")
                                        .description("액세스토큰(로그인 완료)")
                                        .type(JsonFieldType.STRING)
                                        .optional(),
                                fieldWithPath("preSignupId")
                                        .description("임시ID(회원가입시 사용)")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                        )
                ));
    }

    @Test
    @WithMockUser
    void signupDocs() throws Exception {
        // given
        SignupRequest signupRequest = new SignupRequest();
        ReflectionTestUtils.setField(signupRequest, "preSignupId", 1L);
        ReflectionTestUtils.setField(signupRequest, "nickname", "초코우유");
        ReflectionTestUtils.setField(signupRequest, "agreements", List.of(
                TermsAgreement.of(1L, true),
                TermsAgreement.of(2L, true)
        ));

        OAuthLoginResponse signupResponse = OAuthLoginResponse.existingUser(
                "access-token-sample",
                null,
                null
        );

        given(userService.signup(anyLong(), anyString(), any()))
                .willReturn(signupResponse);

        mockMvc.perform(post("/auth/signup")
                        .contentType("application/json")
                        .content(toJson(signupRequest)))
                .andExpect(status().isOk())
                .andDo(document("auth/oauth2-signup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("preSignupId").description("사전 발급된 임시 회원가입 ID"),
                                fieldWithPath("nickname").description("사용자 닉네임 (최대 15자)"),
                                fieldWithPath("agreements[].id").description("동의한 약관 ID"),
                                fieldWithPath("agreements[].isAgree").description("해당 약관 동의 여부")
                        ),
                        relaxedResponseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("status")
                                        .description("응답 상태 (EXISTING_USER)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("accessToken")
                                        .description("로그인 완료시 발급된 액세스 토큰")
                                        .type(JsonFieldType.STRING)
                        )
                ));
    }


}
