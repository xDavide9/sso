package com.xdavide9.sso.exception.authentication.api;

/**
 * This exception is thrown when:
 * 1) a new user tries to register a new account
 * 2) an operator or admin changes the username field of a user
 * but the input username they provide is already taken
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException(String message) {
        super(message);
    }
}
