package com.boggle_boggle.bbegok.exception;


import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum Code {
    // 기본 예외
    OK(0, HttpStatus.OK, "Ok"),
    BAD_REQUEST(10000, HttpStatus.BAD_REQUEST, "Bad request"),
    VALIDATION_ERROR(10001, HttpStatus.BAD_REQUEST, "Validation error"),
    NOT_FOUND(10002, HttpStatus.NOT_FOUND, "Requested resource is not found"),
    INTERNAL_ERROR(20000, HttpStatus.INTERNAL_SERVER_ERROR, "Internal error"),
    DATA_ACCESS_ERROR(20001, HttpStatus.INTERNAL_SERVER_ERROR, "Data access error"),
    UNAUTHORIZED(40000, HttpStatus.UNAUTHORIZED, "User unauthorized"),
    METHOD_NOT_ALLOWED(60000, HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed"),

    //Redis 예외
    SEARCH_LOG_NOT_EXIST(20002, HttpStatus.NOT_FOUND, "Search log not exist"),

    // 책 관련 예외
    BOOK_NOT_FOUND(11000, HttpStatus.NOT_FOUND, "Book not found"),
    INVALID_ISBN(11001, HttpStatus.BAD_REQUEST, "Invalid ISBN"),
    BOOK_ALREADY_EXISTS(11002, HttpStatus.CONFLICT, "Book already exists in the reading list"),

    // 독서록 관련 예외
    READING_RECORD_NOT_FOUND(12000, HttpStatus.NOT_FOUND, "Reading record not found"),
    INVALID_READING_DATE(12001, HttpStatus.BAD_REQUEST, "Invalid reading date"),
    READING_RECORD_ALREADY_EXIST(12002, HttpStatus.CONFLICT, "Reading Record already exists for this book"),

    // 노트 관련 예외
    NOTE_NOT_FOUND(12500, HttpStatus.NOT_FOUND, "Reading Note not found"),

    // 사용자 관련 예외
    USER_NOT_FOUND(13000, HttpStatus.NOT_FOUND, "User not found"),
    DUPLICATE_USERNAME(13001, HttpStatus.CONFLICT, "Username already exists"),
    INVALID_PASSWORD(13002, HttpStatus.BAD_REQUEST, "Invalid password format"),

    // API 관련 예외
    API_REQUEST_FAILED(14000, HttpStatus.SERVICE_UNAVAILABLE, "Failed to fetch data from external API"),
    API_RESPONSE_PARSE_ERROR(14001, HttpStatus.INTERNAL_SERVER_ERROR, "Failed to parse API response"),
    API_RATE_LIMIT_EXCEEDED(14002, HttpStatus.TOO_MANY_REQUESTS, "API rate limit exceeded"),

    // 기능 관련 예외
    INVALID_RATING(15000, HttpStatus.BAD_REQUEST, "Invalid rating value"),
    REVIEW_TOO_LONG(15001, HttpStatus.BAD_REQUEST, "Review exceeds maximum length"),
    INVALID_DATE_RANGE(15002, HttpStatus.BAD_REQUEST, "Invalid date range for reading statistics"),

    // 권한 관련 예외
    ACCESS_DENIED(16000, HttpStatus.FORBIDDEN, "Access denied to requested resource"),
    INSUFFICIENT_PERMISSIONS(16001, HttpStatus.FORBIDDEN, "Insufficient permissions for this action"),
    GUEST_DENIED_ACCESS(16003, HttpStatus.FORBIDDEN, "Guest denied acess user's resource"),

    // JWT 관련 예외
    JWT_INVALID_SIGNATURE(40001, HttpStatus.UNAUTHORIZED, "Invalid JWT signature"),
    JWT_INVALID_TOKEN(40002, HttpStatus.UNAUTHORIZED, "Invalid JWT token"),
    JWT_EXPIRED_TOKEN(40003, HttpStatus.UNAUTHORIZED, "Expired JWT token"),
    JWT_UNSUPPORTED_TOKEN(40004, HttpStatus.UNAUTHORIZED, "Unsupported JWT token"),
    JWT_INVALID_CLAIMS(40005, HttpStatus.UNAUTHORIZED, "JWT claims string is empty"),

    //토큰 관련 예외
    TOKEN_NOT_EXPIRED(50006, HttpStatus.UNPROCESSABLE_ENTITY ,"Access token is not expired yet" ),
    REFRESH_TOKEN_NOT_FOUND(50007, HttpStatus.UNAUTHORIZED, "Refresh token not found"),
    INVALID_REFRESH_TOKEN(50008,HttpStatus.UNAUTHORIZED ,  "Invalid refresh token"),
    INVALID_ACCESS_TOKEN(50005,HttpStatus.UNAUTHORIZED ,  "Invalid access token"),
    EMPTY_COOKIE(50009,HttpStatus.UNAUTHORIZED, "Empty COOKIE"),
    EMPTY_ACCESS_TOKEN(50010,HttpStatus.UNAUTHORIZED, "Access token Empty"),

    //서재 예외
    DUPLICATE_LIBRARY_NAME(600000, HttpStatus.CONFLICT, "library name already exists"),
    LIBRARY_NOT_FOUND(600001, HttpStatus.NOT_FOUND, "library not found");


    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage(Throwable e) {
        return this.getMessage(this.getMessage() + " - " + e.getMessage());
        // 결과 예시 - "Validation error - Reason why it isn't valid"
    }

    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(this.getMessage());
    }

    public static Code valueOf(HttpStatus httpStatus) {
        if (httpStatus == null) {
            throw new GeneralException("HttpStatus is null.");
        }

        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getHttpStatus() == httpStatus)
                .findFirst()
                .orElseGet(() -> {
                    if (httpStatus.is4xxClientError()) {
                        return Code.BAD_REQUEST;
                    } else if (httpStatus.is5xxServerError()) {
                        return Code.INTERNAL_ERROR;
                    } else {
                        return Code.OK;
                    }
                });
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", this.name(), this.getCode());
    }
}
