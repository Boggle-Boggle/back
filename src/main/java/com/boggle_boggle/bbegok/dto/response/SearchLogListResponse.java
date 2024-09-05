package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.BookData;
import com.boggle_boggle.bbegok.dto.SearchLogs;
import com.boggle_boggle.bbegok.redis.SearchLogRedis;
import com.boggle_boggle.bbegok.utils.LocalDateTimeUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@Builder
public class SearchLogListResponse {
    private List<SearchLogs> recentSearchLogs;

    public static SearchLogListResponse from(List<SearchLogRedis> recentSearchLogs) {
        return SearchLogListResponse.builder()
                .recentSearchLogs(
                        recentSearchLogs.stream().map(
                                log -> SearchLogs.builder()
                                        .keyword(log.getKeyword())
                                        .createdAt(LocalDateTimeUtil.StringToLocalDate(log.getCreatedAt()))
                                        .build()
                        ).collect(Collectors.toList())
                )
                .build();
    }
}
