package com.xdavide9.sso.exception.user.api;

import com.xdavide9.sso.util.ValidatorService;

/**
 * This exception is thrown when {@link ValidatorService} detects
 * an invalid phone number.
 */
public class InvalidPhoneNumberException extends RuntimeException {
    public InvalidPhoneNumberException(String message) {
        super(message);
    }

    public InvalidPhoneNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}
