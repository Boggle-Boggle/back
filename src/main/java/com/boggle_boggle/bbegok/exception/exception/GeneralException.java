package com.boggle_boggle.bbegok.exception.exception;

import com.boggle_boggle.bbegok.exception.Code;
import lombok.Getter;

import java.util.Map;

@Getter
public class GeneralException extends RuntimeException{
    private final Code errorCode;
    private final Object data;

    public GeneralException() {
        super(Code.INTERNAL_ERROR.getMessage());
        this.errorCode = Code.INTERNAL_ERROR;
        this.data = null;
    }

    public GeneralException(String message) {
        super(Code.INTERNAL_ERROR.getMessage(message));
        this.errorCode = Code.INTERNAL_ERROR;
        this.data = null;
    }

    public GeneralException(String message, Throwable cause) {
        super(Code.INTERNAL_ERROR.getMessage(message), cause);
        this.errorCode = Code.INTERNAL_ERROR;
        this.data = null;
    }

    public GeneralException(Throwable cause) {
        super(Code.INTERNAL_ERROR.getMessage(cause));
        this.errorCode = Code.INTERNAL_ERROR;
        this.data = null;
    }

    public GeneralException(Code errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = null;
    }

    public GeneralException(Code errorCode, String message) {
        super(errorCode.getMessage(message));
        this.errorCode = errorCode;
        this.data = null;
    }

    public GeneralException(Code errorCode, Map<String, Object> data) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = data;
    }


    public GeneralException(Code errorCode, String message, Object data) {
        super(errorCode.getMessage(message));
        this.errorCode = errorCode;
        this.data = data;
    }

    public GeneralException(Code errorCode, String message, Throwable cause) {
        super(errorCode.getMessage(message), cause);
        this.errorCode = errorCode;
        this.data = null;
    }

    public GeneralException(Code errorCode, Throwable cause) {
        super(errorCode.getMessage(cause), cause);
        this.errorCode = errorCode;
        this.data = null;
    }
}
