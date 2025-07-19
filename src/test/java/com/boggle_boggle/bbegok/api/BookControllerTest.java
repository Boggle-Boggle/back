package com.boggle_boggle.bbegok.api;

import com.boggle_boggle.bbegok.AbstractRestDocsTests;
import com.boggle_boggle.bbegok.RestDocsConfiguration;
import com.boggle_boggle.bbegok.controller.BookController;
import com.boggle_boggle.bbegok.dto.BookData;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.SearchBookListResponse;
import com.boggle_boggle.bbegok.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;


@WebMvcTest(controllers = BookController.class)
@Import(RestDocsConfiguration.class)
class BookControllerTest extends AbstractRestDocsTests {

    @MockBean
    private BookService bookService;

    @Test
    @WithMockUser(username = "1")
    void searchBooksDocs() throws Exception {
        //BookData 리스트 생성
        List<BookData> dummyBookList = List.of(
                BookData.builder()
                        .title("북마녀의 19금 웹소설 단어 사전")
                        .isbn("K852833575")
                        .author("북마녀 (지은이)")
                        .cover(null)
                        .publisher("허들링북스")
                        .adult(true)
                        .myBook(false)
                        .build()
        );

        //SearchBookListResponse 더미 생성
        SearchBookListResponse dummyResponse = SearchBookListResponse.builder()
                .pageNum(1)
                .totalResultCnt(32)
                .itemsPerPage(10)
                .bookList(dummyBookList)
                .build();

        //서비스에서 위 응답을 리턴하도록 지정
        given(bookService.getSearchBookList(anyString(), anyInt(), anyString()))
                .willReturn(dummyResponse);

        final FieldDescriptor[] searchBookListFields = {
                fieldWithPath("pageNum").description("현재 페이지 번호").type(JsonFieldType.NUMBER),
                fieldWithPath("totalResultCnt").description("전체 결과 개수").type(JsonFieldType.NUMBER),
                fieldWithPath("itemsPerPage").description("페이지당 아이템 수").type(JsonFieldType.NUMBER),
                fieldWithPath("items[].isbn").description("ISBN").type(JsonFieldType.STRING),
                fieldWithPath("items[].title").description("제목").type(JsonFieldType.STRING),
                fieldWithPath("items[].author").description("저자").type(JsonFieldType.STRING),
                fieldWithPath("items[].cover").description("표지 URL").type(JsonFieldType.STRING).optional(),
                fieldWithPath("items[].publisher").description("출판사").type(JsonFieldType.STRING),
                fieldWithPath("items[].isAdult").description("성인용 콘텐츠 여부").type(JsonFieldType.BOOLEAN),
                fieldWithPath("items[].isMyBook").description("나의 책 여부").type(JsonFieldType.BOOLEAN)
        };

        //그 후 기존 테스트 코드 유지
        mockMvc.perform(get("/books")
                        .param("query", "19금")
                        .param("pageNum", "1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(document("books/search-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("query").description("검색어"),
                                parameterWithName("pageNum").description("페이지 번호 (1부터 시작)")
                        ),
                        relaxedResponseFields(
                                beneathPath("data").withSubsectionId("data"),
                                searchBookListFields
                        )
                ));
    }

    @Test
    @WithMockUser(username = "1")
    void getBookDocs() throws Exception {
        // BookDetailResponse 더미 생성
        BookDetailResponse dummyDetail = BookDetailResponse.builder()
                .title("백귀야행 19")
                .isbn("8952760549")
                .author("이마 이치코 (지은이)")
                .pubDate(LocalDateTime.of(2011, 2, 11, 0, 0))
                .cover("https://image.aladin.co.kr/product/883/16/cover200/8952760549_1.jpg")
                .publisher("시공사(만화)")
                .genre("국내도서>만화>본격장르만화>호러/스릴러")
                .plot("이마 이치코의 괴기환상담...")
                .page(232)
                .link("https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=8831672&partner=openAPI&start=api")
                .adult(false)
                .myBook(false)
                .build();

        given(bookService.getBook(anyString(), anyString()))
                .willReturn(dummyDetail);

        final FieldDescriptor[] bookDetailFields = {
                fieldWithPath("title").description("제목").type(JsonFieldType.STRING),
                fieldWithPath("isbn").description("ISBN").type(JsonFieldType.STRING),
                fieldWithPath("author").description("저자").type(JsonFieldType.STRING),
                fieldWithPath("pubDate").description("출판일").type(JsonFieldType.STRING),
                fieldWithPath("cover").description("표지 URL").type(JsonFieldType.STRING),
                fieldWithPath("publisher").description("출판사").type(JsonFieldType.STRING),
                fieldWithPath("genre").description("장르").type(JsonFieldType.STRING),
                fieldWithPath("plot").description("줄거리").type(JsonFieldType.STRING),
                fieldWithPath("page").description("페이지 수").type(JsonFieldType.NUMBER),
                fieldWithPath("link").description("상품 링크").type(JsonFieldType.STRING),
                fieldWithPath("adult").description("성인용 콘텐츠 여부").type(JsonFieldType.BOOLEAN),
                fieldWithPath("isMyBook").description("나의 책 여부").type(JsonFieldType.BOOLEAN)
        };

        mockMvc.perform(get("/books/{isbn}", "8952760549")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(document("books/detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("isbn").description("ISBN")
                        ),
                        relaxedResponseFields(
                                beneathPath("data").withSubsectionId("data"),
                                bookDetailFields
                        )
                ));
    }



}
