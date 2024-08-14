package com.boggle_boggle.bbegok.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter @ToString
public class NoteImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteImageSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="note_seq", nullable = false)
    private Note note;

    @Column(name = "file_name", length = 200, nullable = false)
    private String fileName;

    @Column(name = "image_url", length = 255, nullable = false)
    private String imageUrl;
}
