package com.boggle_boggle.bbegok.entity;

import com.boggle_boggle.bbegok.dto.PagesDto;
import com.boggle_boggle.bbegok.entity.embed.CrudDate;
import com.boggle_boggle.bbegok.entity.embed.Pages;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @ToString
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteSeq;

    @Embedded
    private CrudDate crudDate = new CrudDate();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_record_seq", nullable = false)
    private ReadingRecord readingRecord;

    @OneToOne
    @JoinColumn(name = "read_date_seq")
    private ReadDate readDate;

    @Column(name = "title", length = 30)
    private String title;

    @Column(name = "content", length = 255)
    private String content;

    @Column(name = "page", nullable = true)
    private Integer page;

    @Embedded
    private Pages pages;

    @Type(JsonType.class)
    @Column(name = "tags", columnDefinition = "longtext", nullable = false)
    private List<String> tags = new ArrayList<>();

    @Column(name = "selected_date")
    private LocalDateTime selectedDate;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoteImage> imageUrlList = new ArrayList<>();

    protected Note(){}

    private Note(ReadingRecord readingRecord){
        this.readingRecord = readingRecord;
    }

    public static Note createNote(ReadingRecord readingRecord){
        return new Note(readingRecord);
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateReadDate(ReadDate readDate) {
        this.readDate = readDate;
    }

    public void updateSelectedDate(LocalDateTime selectedDate) {
        this.selectedDate = selectedDate;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updatePage(Integer page) {
        this.page = page;
    }

    public void updatePages(Pages pages) {
        this.pages = pages;
    }

    public void updateTags(List<String> tags) {
        this.tags = tags;
    }
}
