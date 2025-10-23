package com.dongpv.sns.gateway.exception;

import java.io.Serial;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.dongpv.sns.gateway.code.ErrorCode;

import lombok.Getter;

@Getter
@ResponseStatus(HttpStatus.ACCEPTED)
public class CommonException extends RuntimeException {
    private final ErrorCode errorCode;

    @Serial
    private static final long serialVersionUID = 3670539246776446256L;

    public CommonException() {
        this("Uncategorized", null);
    }

    public CommonException(String message) {
        this(message, null);
    }

    public CommonException(ErrorCode errorCode) {
        this(errorCode.getMessage(), errorCode);
    }

    private CommonException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected CommonException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
