package com.boggle_boggle.bbegok.dto.base;

import com.boggle_boggle.bbegok.exception.Code;
import lombok.Getter;

@Getter
public class ErrorResponseDto<T> extends ResponseDto {

    private final T data;

    private ErrorResponseDto(Code errorCode, T data) {
        super(false, errorCode.getCode(), errorCode.getMessage());
        this.data = data;
    }

    public static <T> ErrorResponseDto<T> of(Code errorCode) {
        return new ErrorResponseDto<>(errorCode,(T) null);
    }

    public static <T> ErrorResponseDto<T> of(Code errorCode, T data) {
        return new ErrorResponseDto<>(errorCode, data);
    }
}