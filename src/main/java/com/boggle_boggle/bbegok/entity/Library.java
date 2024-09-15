package com.boggle_boggle.bbegok.entity;

import com.boggle_boggle.bbegok.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@ToString @Getter
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long librarySeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq")
    private User user;

    @Column(name = "library_name", nullable = false)
    private String libraryName;

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadingRecordLibraryMapping> mappingList = new ArrayList<>();

    protected Library(){}

    private Library(User user, String libraryName) {
        this.user = user;
        this.libraryName = libraryName;
    }

    public static Library createLibrary(User user, String libraryName){
        return new Library(user, libraryName);
    }
}
