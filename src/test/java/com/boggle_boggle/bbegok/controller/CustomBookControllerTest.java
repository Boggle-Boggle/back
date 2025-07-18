package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.AbstractRestDocsTests;
import com.boggle_boggle.bbegok.RestDocsConfiguration;
import com.boggle_boggle.bbegok.dto.BookData;
import com.boggle_boggle.bbegok.dto.request.CreateCustomBookRequest;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.SearchBookListResponse;
import com.boggle_boggle.bbegok.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = CustomBookController.class)
@Import(RestDocsConfiguration.class)
class CustomBookControllerTest extends AbstractRestDocsTests {

    @MockBean
    private BookService bookService;

    private final FieldDescriptor[] createBookFields = {
            fieldWithPath("title")
                    .description("책 제목 (필수, 최대 255자)").type(JsonFieldType.STRING),
            fieldWithPath("author")
                    .description("책 저자 (필수, 최대 255자)").type(JsonFieldType.STRING),
            fieldWithPath("publisher")
                    .description("출판사 (최대 255자)").type(JsonFieldType.STRING).optional(),
            fieldWithPath("coverUrl")
                    .description("표지 URL (최대 1000자)").type(JsonFieldType.STRING).optional(),
            fieldWithPath("isbn")
                    .description("ISBN (최대 20자)").type(JsonFieldType.STRING).optional(),
            fieldWithPath("page")
                    .description("페이지 수 (0 이상 정수)").type(JsonFieldType.NUMBER).optional(),
            fieldWithPath("plot")
                    .description("줄거리 (최대 1000자)").type(JsonFieldType.STRING).optional()
    };

    @Test
    @DisplayName("커스텀 책 추가 API")
    @WithMockUser(username = "1")
    void saveCustomBook() throws Exception {
        CreateCustomBookRequest request = new CreateCustomBookRequest();
        request.setTitle("소년이 올까말까");
        request.setAuthor("금강");
        request.setPage(300);
        request.setPlot("소년이 올랑말랑 고민하는 이야기");

        doNothing().when(bookService).saveCustomBooks(any(), anyString());

        mockMvc.perform(post("/book")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(document("book/save",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(createBookFields)
                ));
    }

    @Test
    @DisplayName("커스텀 책 수정 API")
    @WithMockUser(username = "1")
    void updateCustomBook() throws Exception {
        CreateCustomBookRequest request = new CreateCustomBookRequest();
        request.setTitle("수정된 책 제목");
        request.setAuthor("수정된 저자");

        doNothing().when(bookService).updateCustomBooks(anyLong(), any(), anyString());

        mockMvc.perform(put("/book/{id}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(document("book/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("수정할 책의 ID")
                        ),
                        requestFields(createBookFields)
                ));
    }

}
