package com.boggle_boggle.bbegok.dto.base;

import com.boggle_boggle.bbegok.exception.Code;
import lombok.Getter;

/** 사용자 정보를 반환할땐 DataResponseDto<UserDto>로,
 * 책 정보를 반환할땐 DataResponseDto<BookDto>로 할 수 있음
 */
@Getter
public class DataResponseDto<T> extends ResponseDto {

    private final T data;

    private DataResponseDto(T data) {
        super(true, Code.OK.getCode(), Code.OK.getMessage());
        this.data = data;
    }

    private DataResponseDto(T data, String message) {
        super(true, Code.OK.getCode(), message);
        this.data = data;
    }

    public static <T> DataResponseDto<T> of(T data) {
        return new DataResponseDto<>(data);
    }

    public static <T> DataResponseDto<T> of(T data, String message) {
        return new DataResponseDto<>(data, message);
    }

    public static <T> DataResponseDto<T> empty() {
        return new DataResponseDto<>(null);
    }
}