package com.xdavide9.sso.exception.user.validation;

import com.xdavide9.sso.util.ValidatorService;

/**
 * This exception is thrown when {@link ValidatorService} detects an invalid username
 */
public class InvalidUsernameException extends RuntimeException {
    public InvalidUsernameException(String message) {
        super(message);
    }
}
