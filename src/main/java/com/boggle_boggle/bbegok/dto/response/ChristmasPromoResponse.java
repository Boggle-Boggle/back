package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.enums.ReadingStyleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChristmasPromoResponse {
    private SummaryData summary;
    private RankingData ranking;
    private ReadingStyleData readingStyle;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SummaryData {
        private int totalBookCount;
        private int excludePendingCount;
        private int totalNoteCount;
        private Double averageRating;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RankingData {
        private List<BestBook> bestBooks;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class BestBook {
        private int rank;
        private String imageUrl;
        private String title;
        private String publisher;
        private int readCount;
        private int noteCount;
        private Double rating;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ReadingStyleData {
        private ReadingStyleType styleType;
        private String styleName;
        //private String styleDescription;

        public static ReadingStyleData of(ReadingStyleType type) {
            return ReadingStyleData.builder()
                    .styleType(type)
                    .styleName(type.getName())
                    //.styleDescription(type.getDescription())
                    .build();
        }
    }
}
