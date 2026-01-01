package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.response.ChristmasPromoResponse;
import com.boggle_boggle.bbegok.dto.response.ChristmasPromoResponse.*;
import com.boggle_boggle.bbegok.entity.ChristmasPromoLog;
import com.boggle_boggle.bbegok.entity.ReadDate;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import com.boggle_boggle.bbegok.enums.ReadingStyleType;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.ChristmasPromoLogRepository;
import com.boggle_boggle.bbegok.repository.NoteRepository;
import com.boggle_boggle.bbegok.repository.ReadingRecordRepository;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChristmasPromoService {

    private final UserRepository userRepository;
    private final ReadingRecordRepository readingRecordRepository;
    private final NoteRepository noteRepository;
    private final ChristmasPromoLogRepository christmasPromoLogRepository;

    @Transactional
    public ChristmasPromoResponse getChristmasPromo(String userSeq) {
        User user = getUser(userSeq);

        SummaryData summary = buildSummaryData(user);
        RankingData ranking = buildRankingData(user);
        ReadingStyleData readingStyle = buildReadingStyleData(user);

        // 로그 저장
        savePromoLog(user, summary, ranking, readingStyle);

        return ChristmasPromoResponse.builder()
                .summary(summary)
                .ranking(ranking)
                .readingStyle(readingStyle)
                .build();
    }

    private void savePromoLog(User user, SummaryData summary, RankingData ranking, ReadingStyleData readingStyle) {
        String bestBookTitle = null;
        Double bestBookRating = null;

        if (ranking.getBestBooks() != null && !ranking.getBestBooks().isEmpty()) {
            BestBook firstBook = ranking.getBestBooks().get(0);
            bestBookTitle = firstBook.getTitle();
            bestBookRating = firstBook.getRating();
        }

        ChristmasPromoLog log = ChristmasPromoLog.create(
                user,
                summary.getTotalBookCount(),
                summary.getExcludePendingCount(),
                summary.getTotalNoteCount(),
                summary.getAverageRating(),
                bestBookTitle,
                bestBookRating,
                readingStyle.getStyleType()
        );

        christmasPromoLogRepository.save(log);
    }

    private User getUser(String userSeq) {
        User user = userRepository.findByUserSeqAndIsDeleted(Long.valueOf(userSeq), false);
        if (user == null) {
            if (userRepository.countByUserSeqAndIsDeleted(Long.valueOf(userSeq), true) > 0) {
                throw new GeneralException(Code.USER_ALREADY_WITHDRAWN);
            }
            throw new GeneralException(Code.USER_NOT_FOUND);
        }
        return user;
    }

    private SummaryData buildSummaryData(User user) {
        int totalBookCount = readingRecordRepository.countTotalBooks(user);
        int excludePendingCount = readingRecordRepository.countBooksExcludePending(user);
        int totalNoteCount = noteRepository.countByUser(user);
        Double averageRating = readingRecordRepository.getAverageRating(user);

        return SummaryData.builder()
                .totalBookCount(totalBookCount)
                .excludePendingCount(excludePendingCount)
                .totalNoteCount(totalNoteCount)
                .averageRating(averageRating != null ? Math.round(averageRating * 10) / 10.0 : 0.0)
                .build();
    }

    private RankingData buildRankingData(User user) {
        List<ReadingRecord> allRecords = readingRecordRepository.findAllByUser(user);
        int currentYear = 2025;

        List<BestBook> bestBooks = allRecords.stream()
                .filter(record -> hasReadDateThisYear(record, currentYear))
                .map(record -> {
                    int readCount = (int) record.getReadDateList().stream()
                            .filter(rd -> rd.getStatus() == ReadStatus.completed)
                            .count();
                    int noteCount = record.getNoteList().size();
                    Double rating = record.getRating();

                    return BestBook.builder()
                            .rank(0)
                            .imageUrl(record.getBook().getImageUrl())
                            .title(record.getBook().getTitle())
                            .publisher(record.getBook().getPublisher())
                            .readCount(readCount)
                            .noteCount(noteCount)
                            .rating(rating != null ? rating : 0.0)
                            .build();
                })
                .sorted((a, b) -> {
                    // 1. 별점 높은순 (null은 가장 낮게)
                    int ratingCompare = compareNullable(b.getRating(), a.getRating());
                    if (ratingCompare != 0) return ratingCompare;

                    // 2. 노트 많은순
                    int noteCompare = Integer.compare(b.getNoteCount(), a.getNoteCount());
                    if (noteCompare != 0) return noteCompare;

                    // 3. 회독수 많은순
                    return Integer.compare(b.getReadCount(), a.getReadCount());
                })
                .limit(3)
                .collect(Collectors.toList());

        // rank 설정
        for (int i = 0; i < bestBooks.size(); i++) {
            BestBook original = bestBooks.get(i);
            bestBooks.set(i, BestBook.builder()
                    .rank(i + 1)
                    .imageUrl(original.getImageUrl())
                    .title(original.getTitle())
                    .publisher(original.getPublisher())
                    .readCount(original.getReadCount())
                    .noteCount(original.getNoteCount())
                    .rating(original.getRating())
                    .build());
        }

        return RankingData.builder()
                .bestBooks(bestBooks)
                .build();
    }

    private int compareNullable(Double a, Double b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return Double.compare(a, b);
    }

    private boolean hasReadDateThisYear(ReadingRecord record, int year) {
        return record.getReadDateList().stream()
                .anyMatch(rd -> {
                    boolean startThisYear = rd.getStartReadDate() != null
                            && rd.getStartReadDate().getYear() == year;
                    boolean endThisYear = rd.getEndReadDate() != null
                            && rd.getEndReadDate().getYear() == year;
                    return startThisYear || endThisYear;
                });
    }

    private ReadingStyleData buildReadingStyleData(User user) {
        List<ReadingRecord> allRecords = readingRecordRepository.findAllByUser(user);

        if (allRecords.isEmpty()) {
            return ReadingStyleData.of(ReadingStyleType.STARTER);
        }

        // 모든 ReadDate 수집
        List<ReadDate> allReadDates = allRecords.stream()
                .flatMap(r -> r.getReadDateList().stream())
                .collect(Collectors.toList());

        // 완독된 ReadDate들
        List<ReadDate> completedReadDates = allReadDates.stream()
                .filter(rd -> rd.getStatus() == ReadStatus.completed && rd.getEndReadDate() != null)
                .sorted(Comparator.comparing(ReadDate::getEndReadDate))
                .collect(Collectors.toList());

        // 읽는 중인 책 수
        long readingCount = allReadDates.stream()
                .filter(rd -> rd.getStatus() == ReadStatus.reading)
                .map(rd -> rd.getReadingRecord().getReadingRecordSeq())
                .distinct()
                .count();

        // 완독 수
        long completedCount = completedReadDates.size();

        // 1. STARTER: 읽는 중인 책이 동시에 여러 개 (4개 이상)
        if (readingCount >= 4) {
            return ReadingStyleData.of(ReadingStyleType.STARTER);
        }

        // 2. FINISHER: 읽는 중인 책 3개 이하 + 다읽은 책 많음 (5권 이상)
        if (readingCount <= 3 && completedCount >= 5) {
            return ReadingStyleData.of(ReadingStyleType.FINISHER);
        }

        // 완독 기록이 있어야 분석 가능
        if (completedReadDates.size() >= 2) {
            // 평균 완독 기간 계산
            double avgCompletionDays = calculateAverageCompletionDays(completedReadDates);

            // 3. INTENSIVE: 평균 완독기간 7일 이하
            if (avgCompletionDays <= 7) {
                return ReadingStyleData.of(ReadingStyleType.INTENSIVE);
            }

            // 4. LEISURELY: 평균 완독기간 14일 이상
            if (avgCompletionDays >= 14) {
                return ReadingStyleData.of(ReadingStyleType.LEISURELY);
            }

            // 5. WAVE: 월별 완독 권수 편차가 큼
            if (hasHighMonthlyVariance(completedReadDates)) {
                return ReadingStyleData.of(ReadingStyleType.WAVE);
            }

            // 6. STEADY: 완독 후 바로 다음 책 읽은 케이스 3회 이상
            if (countConsecutiveReading(completedReadDates) >= 3) {
                return ReadingStyleData.of(ReadingStyleType.STEADY);
            }
        }

        // 기본값: 데이터가 부족하면 STARTER
        return ReadingStyleData.of(ReadingStyleType.STARTER);
    }

    private double calculateAverageCompletionDays(List<ReadDate> completedReadDates) {
        return completedReadDates.stream()
                .filter(rd -> rd.getStartReadDate() != null && rd.getEndReadDate() != null)
                .mapToLong(rd -> ChronoUnit.DAYS.between(rd.getStartReadDate(), rd.getEndReadDate()))
                .average()
                .orElse(0);
    }

    private boolean hasHighMonthlyVariance(List<ReadDate> completedReadDates) {
        Map<YearMonth, Long> monthlyCount = completedReadDates.stream()
                .filter(rd -> rd.getEndReadDate() != null)
                .collect(Collectors.groupingBy(
                        rd -> YearMonth.from(rd.getEndReadDate()),
                        Collectors.counting()
                ));

        if (monthlyCount.size() < 2) return false;

        Collection<Long> counts = monthlyCount.values();
        long max = Collections.max(counts);
        long min = Collections.min(counts);

        // 편차가 3배 이상이면 파도형
        return max >= min * 3;
    }

    private int countConsecutiveReading(List<ReadDate> completedReadDates) {
        if (completedReadDates.size() < 2) return 0;

        int consecutiveCount = 0;
        for (int i = 0; i < completedReadDates.size() - 1; i++) {
            LocalDateTime currentEnd = completedReadDates.get(i).getEndReadDate();
            LocalDateTime nextStart = completedReadDates.get(i + 1).getStartReadDate();

            if (currentEnd != null && nextStart != null) {
                long daysBetween = ChronoUnit.DAYS.between(currentEnd, nextStart);
                // 7일 이내에 다음 책을 시작했으면 연속 독서로 판단
                if (daysBetween >= 0 && daysBetween <= 7) {
                    consecutiveCount++;
                }
            }
        }
        return consecutiveCount;
    }
}
