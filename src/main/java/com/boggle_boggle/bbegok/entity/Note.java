package com.boggle_boggle.bbegok.entity;

import com.boggle_boggle.bbegok.entity.embed.CrudDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

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
    @JoinColumn(name = "reding_record_seq", nullable = false)
    private ReadingRecord readingRecord;

    @Column(name = "title", length = 30, nullable = false)
    private String title;

    @Column(name = "content", length = 255, nullable = false)
    private String content;

    @Column(name = "page", nullable = true)
    private Integer page;

    @Column(name = "tags", length = 255, nullable = true)
    private String tags;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoteImage> imageUrlList = new ArrayList<>();

    protected Note(){}

    private Note(ReadingRecord readingRecord, String title, String content){
        this.readingRecord = readingRecord;
        this.title = title;
        this.content = content;
    }

    public static Note createNote(ReadingRecord readingRecord, String title, String content){
        return new Note(readingRecord, title, content);
    }

    public void updateNote(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
