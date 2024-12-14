package com.boggle_boggle.bbegok.entity;

import com.boggle_boggle.bbegok.dto.ReadDateDto;
import com.boggle_boggle.bbegok.entity.embed.CrudDate;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
public class ReadingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long readingRecordSeq;

    @Embedded
    private CrudDate crudDate = new CrudDate();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_seq", nullable = false)
    private Book book;

    @OneToMany(mappedBy = "readingRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadDate> readDateList = new ArrayList<>();

    @OneToMany(mappedBy = "readingRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> noteList = new ArrayList<>();

    @OneToMany(mappedBy = "readingRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadingRecordLibraryMapping> mappingList = new ArrayList<>();

    @Column(name = "is_book_visible")
    private Boolean isBooksVisible;

    @Column(name = "rating")
    private Double rating;

    @Enumerated(EnumType.STRING)  // ENUM을 스트링으로 저장
    @Column(name = "status", nullable = false)
    private ReadStatus status;

    protected ReadingRecord(){}

    private ReadingRecord(User user, Book book, LocalDateTime readStartDate, LocalDateTime readEndDate,
                          List<Library> libraries, Double rating, Boolean visible, ReadStatus readStatus) {
        this.user = user;
        this.book = book;
        this.rating = rating;
        this.isBooksVisible = visible;
        this.status = readStatus;
        addReadDateList(readStartDate, readEndDate);
        addLibraries(libraries);
    }

    public static ReadingRecord createReadingRecord(User user, Book book, LocalDateTime readStartDate, LocalDateTime readEndDate,
                                                    List<Library> libraries, Double rating, Boolean visible, ReadStatus readStatus) {
        return new ReadingRecord(user, book, readStartDate, readEndDate, libraries, rating, visible, readStatus);
    }

    //==수정
    public void update(ReadStatus readStatus,  Double rating, List<ReadDateDto> readDateList,
                       Boolean visible, List<Library> libraries) {
        if(readStatus != null) this.status = readStatus;
        if(rating != null) this.rating = rating;
        if(visible != null) this.isBooksVisible = visible;

        if(readDateList != null) {
            for(ReadDateDto dto : readDateList) addReadDateList(dto.getStartReadDate(), dto.getEndReadDate());
        }

        if(!libraries.isEmpty()) addLibraries(libraries);
    }


    //==연관관계 편의 메소드
    public void addLibraries(List<Library> libraries) {
        for (Library library : libraries) {
            addLibrary(library);
        }
    }
    public void addLibrary(Library library) {
        ReadingRecordLibraryMapping mapping = ReadingRecordLibraryMapping.createReadingRecordLibraryMapping(this, library);
        this.mappingList.add(mapping);
    }
    public void addReadDateList(LocalDateTime readStartDate, LocalDateTime readEndDate) {
        ReadDate readDate = ReadDate.createReadDate(this, readStartDate, readEndDate);
        this.readDateList.add(readDate);
    }
}
