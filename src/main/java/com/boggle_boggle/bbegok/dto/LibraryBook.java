package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.entity.ReadDate;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
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

    public LibraryBook(Long readingRecordId, String title, Double rating,
                       List<ReadDate> readDateList, String imageUrl) {
        this.readingRecordId = readingRecordId;
        this.title = title;
        this.rating = rating;
        this.readingCount = (int) readDateList.stream()
                .filter(readDate -> readDate.getEndReadDate() != null)
                .count();
        readDateList.get(readDateList.size()-1)
        this.recentReadDate = new ReadDateDto();
    }
}
