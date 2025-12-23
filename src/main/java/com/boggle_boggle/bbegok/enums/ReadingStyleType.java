package com.boggle_boggle.bbegok.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReadingStyleType {
    STEADY("꾸준히 읽는 현실파 독서러", "독서기록 날짜가 다양하며, 완독 후 바로 다음 책을 읽은 케이스가 3회 이상이에요"),
    INTENSIVE("몰아서 읽는 집중형 독서러", "평균 완독기간이 1주일 이하로 짧아요"),
    LEISURELY("천천히 즐기는 여유형 독서러", "평균 완독기간이 2주 이상으로 여유롭게 읽어요"),
    WAVE("읽을땐 몰입하는 파도형 독서러", "월별 완독 권수 편차가 크고, 특정 달에 집중해서 읽어요"),
    STARTER("일단 펼쳐보는 시작형 독서러", "동시에 읽고 있는 책이 여러 권이에요"),
    FINISHER("끝까지 읽는 완주형 독서러", "읽는 중인 책이 적고, 다 읽은 책이 많아요");

    private final String name;
    private final String description;
}
