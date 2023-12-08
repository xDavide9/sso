package com.xdavide9.sso.exception.authentication.api;

import com.xdavide9.sso.user.User;
/**
 * This exception is thrown when the input password is not
 * the same as the one stored in database for a specific {@link User}
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException(String message) {
        super(message);
    }
}
