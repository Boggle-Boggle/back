package com.boggle_boggle.bbegok.entity;

import com.boggle_boggle.bbegok.dto.ReadDateDto;
import com.boggle_boggle.bbegok.dto.request.NewReadingRecordRequest;
import com.boggle_boggle.bbegok.entity.embed.CrudDate;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@ToString
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uq_reading_record_user_book", columnNames = {"user_seq", "book_seq"}))
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

    protected ReadingRecord(){}

    private ReadingRecord(User user, Book book){
        this.user = user;
        this.book = book;
    }

    public static ReadingRecord createReadingRecord(User user,
                                                    Book book,
                                                    NewReadingRecordRequest record,
                                                    List<Library> libraries) {
        ReadingRecord readingRecord = new ReadingRecord(user, book);
        readingRecord.rating = record.getRating();
        readingRecord.isBooksVisible = record.getIsVisible();
        readingRecord.addReadDateList(record.getStartReadDate(), record.getEndReadDate(), record.getReadStatus());
        readingRecord.addLibraries(libraries);

        return readingRecord;
    }


    //==수정
    public void update(ReadStatus readStatus,  Double rating, List<ReadDateDto> readDateList,
                       Boolean visible, List<Library> libraries, ReadStatus status) {
        if(rating != null) this.rating = rating;
        if(visible != null) this.isBooksVisible = visible;

        if(readDateList != null) {
            for(ReadDateDto dto : readDateList) addReadDateList(dto.getStartReadDate(), dto.getEndReadDate(), status);
        }

        addLibraries(libraries);
    }


    //==연관관계 편의 메소드
    public void addLibraries(List<Library> libraries) {
        for (Library library : libraries) {
            addLibrary(library);
        }
    }
    public void addLibrary(Library library) {
        if(library == null) return;
        ReadingRecordLibraryMapping mapping = ReadingRecordLibraryMapping.createReadingRecordLibraryMapping(this, library);
        this.mappingList.add(mapping);
    }
    public void addReadDateList(LocalDateTime readStartDate, LocalDateTime readEndDate, ReadStatus status) {
        ReadDate readDate = ReadDate.createReadDate(this, readStartDate, readEndDate, status);
        updateReadDateList(readDate);
    }

    public void updateReadDateList(ReadDate readDate) {
        this.readDateList.add(readDate);
    }

    public void updateRating(Double rating) {
        this.rating = rating;
    }

    public void updateIsVisible(Boolean visible) {
        this.isBooksVisible = visible;
    }

    public void removeReadDate(ReadDate readDate) {
        boolean b = this.readDateList.remove(readDate);
    }
}
