package com.boggle_boggle.bbegok.api;

import com.boggle_boggle.bbegok.AbstractRestDocsTests;
import com.boggle_boggle.bbegok.RestDocsConfiguration;
import com.boggle_boggle.bbegok.controller.BookController;
import com.boggle_boggle.bbegok.controller.ReadingRecordController;
import com.boggle_boggle.bbegok.dto.BookData;
import com.boggle_boggle.bbegok.dto.request.CustomBookRecordRequest;
import com.boggle_boggle.bbegok.dto.request.NewReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.request.NormalBookRecordRequest;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.ReadingRecordIdResponse;
import com.boggle_boggle.bbegok.dto.response.SearchBookListResponse;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.service.BookService;
import com.boggle_boggle.bbegok.service.ReadingRecordService;
import com.boggle_boggle.bbegok.testfactory.BookTestFactory;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ReadingRecordController.class)
@Import(RestDocsConfiguration.class)
class ReadingRecordControllerTest extends AbstractRestDocsTests {

    @MockBean
    private ReadingRecordService readingRecordService;

    @Test
    @DisplayName("일반책 독서기록 저장")
    @WithMockUser(username = "1")
    void saveNormalBookDocs() throws Exception {
        // given
        NormalBookRecordRequest request = new NormalBookRecordRequest();
        request.setIsbn("8998139766");
        request.setReadStatus(ReadStatus.COMPLETED);
        request.setRating(4.5);
        request.setStartReadDate(LocalDateTime.now().withNano(0));
        request.setEndReadDate(LocalDateTime.now().plusDays(1).withNano(0));
        request.setLibraryIdList(List.of(1L, 2L));
        request.setIsVisible(true);

        given(readingRecordService.save(any(), anyString()))
                .willReturn(ReadingRecordIdResponse.of(123L));

        // when + then
        mockMvc.perform(post("/reading-record")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(document("reading-record/save-normal",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        relaxedRequestFields(normalBookRequestFields),
                        relaxedResponseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("readingRecordId").description("생성된 독서기록 ID").type(JsonFieldType.NUMBER)
                        )
                ));
    }

    @Test
    @DisplayName("커스텀 책 독서기록 저장")
    @WithMockUser(username = "1")
    void saveCustomBookDocs() throws Exception {
        // given
        CustomBookRecordRequest request = new CustomBookRecordRequest();
        request.setCustomBook(BookTestFactory.createCustomBookRequest());
        request.setReadStatus(ReadStatus.COMPLETED);
        request.setRating(4.5);
        request.setStartReadDate(LocalDateTime.now().withNano(0));
        request.setEndReadDate(LocalDateTime.now().plusDays(1).withNano(0));
        request.setLibraryIdList(List.of(1L, 2L));
        request.setIsVisible(true);

        given(readingRecordService.save(any(), anyString()))
                .willReturn(ReadingRecordIdResponse.of(123L));

        // when + then
        mockMvc.perform(post("/reading-record")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(document("reading-record/save-custom",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        relaxedRequestFields(customBookRequestFields),
                        relaxedResponseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("readingRecordId").description("생성된 독서기록 ID").type(JsonFieldType.NUMBER)
                        )
                ));
    }


    private final FieldDescriptor[] commonBookRequestFields = {
            fieldWithPath("readStatus")
                    .description("상태 (필수, (READING, COMPLETED, DROPPED))").type(JsonFieldType.STRING),
            fieldWithPath("rating")
                    .description("평점 (0.0 ~ 5.0)").type(JsonFieldType.NUMBER).optional(),
            fieldWithPath("startReadDate")
                    .description("읽기 시작일 (yyyy-MM-dd'T'HH:mm:ss)").type(JsonFieldType.STRING).optional(),
            fieldWithPath("endReadDate")
                    .description("읽기 종료일 (yyyy-MM-dd'T'HH:mm:ss)").type(JsonFieldType.STRING).optional(),
            fieldWithPath("libraryIdList")
                    .description("포함할 그룹책장 ID 리스트").type(JsonFieldType.ARRAY).optional(),
            fieldWithPath("isVisible")
                    .description("필수, 책장 표시 여부").type(JsonFieldType.BOOLEAN)
    };

    private final FieldDescriptor[] normalBookRequestFields = Stream.concat(
            Stream.of(
                    fieldWithPath("bookType")
                            .description("필수, 책 타입 (NORMAL)").type(JsonFieldType.STRING),
                    fieldWithPath("isbn")
                            .description("필수, 일반 책 ISBN").type(JsonFieldType.STRING)
            ),
            Arrays.stream(commonBookRequestFields)
    ).toArray(FieldDescriptor[]::new);

    private final FieldDescriptor[] customBookRequestFields = Stream.concat(
            Stream.of(
                    fieldWithPath("bookType")
                            .description("필수, 책 타입 (CUSTOM)").type(JsonFieldType.STRING),
                    fieldWithPath("customBook")
                            .description("필수, 커스텀 책 정보 객체").type(JsonFieldType.OBJECT),
                    fieldWithPath("customBook.title")
                            .description("필수, 책 제목 (공백 불가, 최대 255자)").type(JsonFieldType.STRING),
                    fieldWithPath("customBook.author")
                            .description("필수, 책 저자 (공백 불가, 최대 255자)").type(JsonFieldType.STRING),
                    fieldWithPath("customBook.publisher")
                            .description("출판사 (최대 255자)").type(JsonFieldType.STRING).optional(),
                    fieldWithPath("customBook.coverUrl")
                            .description("표지 이미지 URL (최대 1000자)").type(JsonFieldType.STRING).optional(),
                    fieldWithPath("customBook.page")
                            .description("페이지 수 (0 이상의 정수)").type(JsonFieldType.NUMBER).optional(),
                    fieldWithPath("customBook.isbn")
                            .description("ISBN (최대 20자)").type(JsonFieldType.STRING).optional(),
                    fieldWithPath("customBook.plot")
                            .description("줄거리 (최대 1000자)").type(JsonFieldType.STRING).optional()
            ),
            Arrays.stream(commonBookRequestFields)
    ).toArray(FieldDescriptor[]::new);


}
