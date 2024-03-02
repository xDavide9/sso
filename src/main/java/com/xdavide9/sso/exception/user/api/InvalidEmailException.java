package com.xdavide9.sso.exception.user.api;

import com.xdavide9.sso.util.ValidatorService;

/**
 * This exception is thrown when {@link ValidatorService} detects
 * an invalid email.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
