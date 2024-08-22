package com.boggle_boggle.bbegok.redis;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class SearchLogRedis {
    private String keyword; //검색어
    private String createdAt; //검색일자
}
