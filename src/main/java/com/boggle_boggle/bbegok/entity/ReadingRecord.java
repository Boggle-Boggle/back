package com.boggle_boggle.bbegok.entity;

import com.boggle_boggle.bbegok.entity.embed.CrudDate;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

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

    @Column(name = "is_book_visible", length = 255, nullable = false)
    private Boolean isBooksVisible;

    @Column(name = "rating", nullable = false)
    private Double rating;

    @Column(name = "status", length = 255, nullable = false)
    private ReadStatus status;

    protected ReadingRecord(){}

    private ReadingRecord(User user, Book book, ReadDate readDate, List<Library> libraries,
                         double rating, boolean visible, ReadStatus readStatus) {
        this.user = user;
        this.book = book;
        this.readDateList.add(readDate);
        this.rating = rating;
        this.isBooksVisible = visible;
        this.status = readStatus;
        System.out.println("사이즈 "+libraries.size());
        addLibraries(libraries);
    }

    public static ReadingRecord createReadingRecord(User user, Book book, ReadDate readDate,
                                                    List<Library> libraries, double rating, boolean visible, ReadStatus readStatus) {
        return new ReadingRecord(user, book, readDate, libraries, rating, visible, readStatus);
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

}
