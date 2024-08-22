package com.boggle_boggle.bbegok.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DeleteRecentSearchRequest {
    private String keyword;
    private String createdAt;
}
