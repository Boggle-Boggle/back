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
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportSeq;

    @Embedded
    private CrudDate crudDate = new CrudDate();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_seq", nullable = false)
    private Book book;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadDate> readDateList = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> noteList = new ArrayList<>();

    @Column(name = "is_book_visible", length = 255, nullable = false)
    private Boolean isBooksVisible;

    @Column(name = "rating", nullable = false)
    private Double rating;

    @Column(name = "status", length = 255, nullable = false)
    private ReadStatus status;

    protected Report(){}

    public static Report createReport(){
        return new Report();
    }
}
