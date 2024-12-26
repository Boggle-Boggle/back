package com.boggle_boggle.bbegok.entity.embed;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@ToString
@Embeddable
public class CrudDate {
    @CreationTimestamp
    @Column(updatable = false) // 생성 시 한 번만 설정
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(insertable = false) // 수정 시에만 업데이트
    private LocalDateTime updateAt;

    private Boolean isDelete;
}
