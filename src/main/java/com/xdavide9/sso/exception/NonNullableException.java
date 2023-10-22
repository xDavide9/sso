package com.xdavide9.sso.exception;

public class NonNullableException extends RuntimeException {

    public NonNullableException() {
    }

    public NonNullableException(String message) {
        super(message);
    }

    public NonNullableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonNullableException(Throwable cause) {
        super(cause);
    }

    public NonNullableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
