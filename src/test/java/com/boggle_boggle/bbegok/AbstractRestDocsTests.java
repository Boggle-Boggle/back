package com.boggle_boggle.bbegok;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
public abstract class AbstractRestDocsTests {

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    protected String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @BeforeEach
    void setUp(
            final WebApplicationContext context,
            final RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(MockMvcResultHandlers.print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
}