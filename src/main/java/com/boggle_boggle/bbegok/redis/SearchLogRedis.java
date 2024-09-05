package com.boggle_boggle.bbegok.redis;

import lombok.*;

import java.util.Objects;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor //역직렬화를 위해 꼭 필요함
public class SearchLogRedis {
    private String keyword; //검색어
    private String createdAt; //검색일자
}
