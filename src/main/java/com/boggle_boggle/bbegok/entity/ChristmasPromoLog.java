package com.boggle_boggle.bbegok.entity;

import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.ReadingStyleType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "christmas_promo_log")
public class ChristmasPromoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 요약 데이터
    @Column(name = "total_book_count")
    private Integer totalBookCount;

    @Column(name = "exclude_pending_count")
    private Integer excludePendingCount;

    @Column(name = "total_note_count")
    private Integer totalNoteCount;

    @Column(name = "average_rating")
    private Double averageRating;

    // 랭킹 데이터 (1위 책 정보만 저장)
    @Column(name = "best_book_title", length = 255)
    private String bestBookTitle;

    @Column(name = "best_book_rating")
    private Double bestBookRating;

    // 독서 스타일
    @Enumerated(EnumType.STRING)
    @Column(name = "reading_style_type")
    private ReadingStyleType readingStyleType;

    protected ChristmasPromoLog() {}

    private ChristmasPromoLog(User user, Integer totalBookCount, Integer excludePendingCount,
                              Integer totalNoteCount, Double averageRating,
                              String bestBookTitle, Double bestBookRating,
                              ReadingStyleType readingStyleType) {
        this.user = user;
        this.totalBookCount = totalBookCount;
        this.excludePendingCount = excludePendingCount;
        this.totalNoteCount = totalNoteCount;
        this.averageRating = averageRating;
        this.bestBookTitle = bestBookTitle;
        this.bestBookRating = bestBookRating;
        this.readingStyleType = readingStyleType;
    }

    public static ChristmasPromoLog create(User user, Integer totalBookCount, Integer excludePendingCount,
                                           Integer totalNoteCount, Double averageRating,
                                           String bestBookTitle, Double bestBookRating,
                                           ReadingStyleType readingStyleType) {
        return new ChristmasPromoLog(user, totalBookCount, excludePendingCount,
                totalNoteCount, averageRating, bestBookTitle, bestBookRating, readingStyleType);
    }
}
