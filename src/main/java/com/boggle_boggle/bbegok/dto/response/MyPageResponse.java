package com.boggle_boggle.bbegok.dto.response;

import lombok.*;

@AllArgsConstructor
@Getter
public class MyPageResponse {
    private String nickname;
    private int totalReadingCnt;
    private int monthlyReadingCnt;
    private int totalNote;

    public static MyPageResponse createMyPageResponse(String nickname, int totalReadingCnt, int monthlyReadingCnt, int totalNote) {
        return new MyPageResponse(nickname, totalReadingCnt, monthlyReadingCnt, totalNote);
    }

}
