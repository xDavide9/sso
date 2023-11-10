package com.xdavide9.sso.exception.authentication.api;

/**
 * This exception is thrown when a user tries to sign up to the application
 * with a password that is too short (less than 8 characters)
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
public class PasswordTooShortException extends RuntimeException{

    /**
     * empty constructor
     * @since 0.0.1-SNAPSHOT
     */
    public PasswordTooShortException() {
    }

    /**
     * super constructor
     * @since 0.0.1-SNAPSHOT
     * @param message message
     */
    public PasswordTooShortException(String message) {
        super(message);
    }
}
