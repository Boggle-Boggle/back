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
    @JoinColumn(name = "report_seq", nullable = false)
    private Report report;

    @Column(name = "content", length = 255, nullable = false)
    private String content;

    @Column(name = "page", nullable = false)
    private Integer page;

    @Column(name = "tags", length = 255, nullable = false)
    private String tags;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoteImage> imageUrlList = new ArrayList<>();

    protected Note(){}

    public static Note createNote(){
        return new Note();
    }
}
