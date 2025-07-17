package com.boggle_boggle.bbegok.entity;

import com.boggle_boggle.bbegok.entity.embed.CrudDate;
import com.boggle_boggle.bbegok.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@ToString
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_user_book", columnNames = {"user_seq", "book_seq"})
        }
)
public class UserFavoriteBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userFavoriteBookSeq;

    @ManyToOne
    private User user;

    @ManyToOne
    private Book book;

    @Embedded
    private CrudDate crudDate = new CrudDate();
}
