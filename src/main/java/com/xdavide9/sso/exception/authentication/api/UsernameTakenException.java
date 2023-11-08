package com.xdavide9.sso.exception.authentication.api;

/**
 * This exception is thrown when a user tries to register a new account but the
 * username they provide is already taken.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class UsernameTakenException extends RuntimeException {
    /**
     * empty constructor
     * @since 0.0.1-SNAPSHOT
     */
    public UsernameTakenException() {
    }

    /**
     * super constructor
     * @param message message
     */
    public UsernameTakenException(String message) {
        super(message);
    }
}
