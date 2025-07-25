package com.boggle_boggle.bbegok.entity;

import com.boggle_boggle.bbegok.entity.embed.CrudDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@ToString
public class Terms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long termsSeq;

    @Embedded
    private CrudDate crudDate = new CrudDate();

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, length = 10000)
    private String content;

    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "isMandatory", nullable = false)
    private Boolean isMandatory;

}
