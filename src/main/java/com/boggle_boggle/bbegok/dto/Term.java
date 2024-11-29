package com.boggle_boggle.bbegok.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class Term {
    private Long id;
    private String title;
    private String content;
    private boolean isMandatory;
}
