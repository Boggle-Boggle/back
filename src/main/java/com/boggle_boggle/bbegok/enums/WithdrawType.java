package com.boggle_boggle.bbegok.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WithdrawType {
    PRIVACY_CONCERN("개인정보 및 보안이 우려돼요"),
    REJOIN_AFTER_WITHDRAWAL("탈퇴 후 신규가입할 거예요"),
    SERVICE_ERROR("서비스 장애와 오류가 있어요"),
    LACK_OF_FEATURES("원하는 기능이 부족해요"),
    TOO_TEDIOUS("기록하기가 번거롭고 귀찮아요"),
    BAD_UI_UX("인터페이스가 불편하거나 직관적이지 않아요"),
    NO_LONGER_NEEDED("더 이상 독서기록이 필요하지 않아요"),
    ETC("기타");

    private final String description;
}