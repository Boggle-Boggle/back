package com.boggle_boggle.bbegok.entity.embed;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Pages {
    private Integer startPage;
    private Integer endPage;
}
