package com.boggle_boggle.bbegok.api;

import com.boggle_boggle.bbegok.AbstractRestDocsTests;
import com.boggle_boggle.bbegok.RestDocsConfiguration;
import com.boggle_boggle.bbegok.controller.TermsController;
import com.boggle_boggle.bbegok.dto.Term;
import com.boggle_boggle.bbegok.dto.response.TermsResponse;
import com.boggle_boggle.bbegok.service.TermsService;
import com.boggle_boggle.bbegok.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TermsController.class)
@Import(RestDocsConfiguration.class)
class TermsControllerTest extends AbstractRestDocsTests {

    @MockBean
    private UserService userService;

    @MockBean
    private TermsService termsService;

    @Test
    void getLatestTermsDocs() throws Exception {
        // given
        List<Term> mockTerms = List.of(
                Term.builder()
                        .id(1L)
                        .version(3)
                        .title("개인정보 수집 및 이용")
                        .content("우리는 당신의 데이터를 이렇게 사용합니다...")
                        .isMandatory(true)
                        .build(),
                Term.builder()
                        .id(2L)
                        .version(3)
                        .title("마케팅 수신 동의")
                        .content("광고를 보낼 수 있습니다.")
                        .isMandatory(false)
                        .build()
        );

        TermsResponse response = TermsResponse.builder()
                .terms(mockTerms)
                .build();

        given(termsService.getLatestTerms()).willReturn(response);

        mockMvc.perform(get("/terms"))
                .andExpect(status().isOk())
                .andDo(document("terms/get-latest-terms",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        relaxedResponseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("terms[].id").description("약관 ID"),
                                fieldWithPath("terms[].version").description("약관 버전"),
                                fieldWithPath("terms[].title").description("약관 제목"),
                                fieldWithPath("terms[].content").description("약관 본문 내용"),
                                fieldWithPath("terms[].mandatory").description("필수 여부 (true=필수 / false=선택)")
                        )
                ));
    }
}
