package com.dongpv.sns.gateway.exception;

import com.dongpv.sns.gateway.code.ErrorCode;

public class JsonSerializationException extends CommonException {
    public JsonSerializationException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
