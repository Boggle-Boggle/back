package com.boggle_boggle.bbegok.api;

import com.boggle_boggle.bbegok.AbstractRestDocsTests;
import com.boggle_boggle.bbegok.RestDocsConfiguration;
import com.boggle_boggle.bbegok.controller.UserController;
import com.boggle_boggle.bbegok.dto.request.WithdrawReasonRequest;
import com.boggle_boggle.bbegok.enums.WithdrawType;
import com.boggle_boggle.bbegok.oauth.service.RevokeService;
import com.boggle_boggle.bbegok.service.QueryService;
import com.boggle_boggle.bbegok.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(RestDocsConfiguration.class)
class UserControllerTest extends AbstractRestDocsTests {

    @MockBean
    private RevokeService revokeService;
    @MockBean
    private QueryService queryService;
    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "test@example.com")
    void deleteUserDocs() throws Exception {
        WithdrawReasonRequest request = new WithdrawReasonRequest();
        request.setWithdrawType(WithdrawType.PRIVACY_CONCERN);
        request.setWithdrawText("개인정보가 불안해서 탈퇴합니다.");

        mockMvc.perform(delete("/user")
                        .contentType("application/json")
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(document("user/delete-user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("withdrawType")
                                        .description("탈퇴 사유 타입 (예: PRIVACY_CONCERN, ETC 등)"),
                                fieldWithPath("withdrawText")
                                        .description("선택 입력 탈퇴 상세 사유 (최대 400자)")
                                        .optional()
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터 (null)")
                        )
                ));
    }
}
