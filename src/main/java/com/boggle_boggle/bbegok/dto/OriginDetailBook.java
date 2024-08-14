package com.boggle_boggle.bbegok.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class OriginDetailBook {
    private List<OriginBookData> item;
}
