package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.dto.response.LibraryResponse;
import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.entity.ReadDate;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@Builder
public class LibraryBook {
    private Long readingRecordId;
    private String title;
    private Double rating;
    private int readingCount;
    private ReadDateDto recentReadDate;
    private String imageUrl;

    public LibraryBook() {

    }

    public static LibraryBook fromEntity(ReadingRecord record) {
        return new LibraryBook(record.getReadingRecordSeq(),
                record.getBook().getTitle(),
                record.getRating(),
                record.getReadDateList(),
                record.getBook().getCoverUrl());
    }

    public LibraryBook(Long readingRecordId, String title, Double rating,
                       List<ReadDate> readDateList, String imageUrl) {
        this.readingRecordId = readingRecordId;
        this.title = title;
        this.rating = rating;
        this.readingCount = (int) readDateList.stream()
                .filter(readDate -> readDate.getEndReadDate() != null)
                .count();
        this.recentReadDate = calculateRecentReadDate(readDateList);
        this.imageUrl = imageUrl;
    }

    public static List<LibraryBook> fromReadingRecordList(List<ReadingRecord> readingRecordList) {
        return readingRecordList.stream()
                .map(LibraryBook::fromEntity).toList();
    }

    private ReadDateDto calculateRecentReadDate(List<ReadDate> readDateList) {
        if(readDateList.isEmpty()) return null;
        else if(readDateList.get(0).getStatus() == ReadStatus.pending) return null;
        else {
            //오름차순 정렬
            readDateList.sort(Comparator.comparingLong(ReadDate::getReadDateSeq));
            ReadDate rd = readDateList.get(readDateList.size()-1);
            return new ReadDateDto(rd.getStartReadDate(), rd.getEndReadDate());
        }
    }

}
