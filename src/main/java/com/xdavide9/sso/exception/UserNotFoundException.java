package com.xdavide9.sso.exception;

/**
 * This class is a custom runtime exception that is thrown when a user is not found.
 * It does not provide any special functionality.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * empty constructor
     * @since 0.0.1-SNAPSHOT
     */
    public UserNotFoundException() {
    }

    /**
     * super constructor
     * @since 0.0.1-SNAPSHOT
     * @param message message
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * super constructor
     * @since 0.0.1-SNAPSHOT
     * @param message message
     * @param cause cause
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * super constructor
     * @since 0.0.1-SNAPSHOT
     * @param cause cause
     */
    public UserNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * super constructor
     * @since 0.0.1-SNAPSHOT
     * @param cause cause
     * @param message message
     * @param enableSuppression enableSuppression
     * @param writableStackTrace writableStackTrace
     */
    public UserNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
