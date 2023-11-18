package com.xdavide9.sso.exception.user.api;

/**
 * This class is a custom runtime exception that is thrown when a user is not found when queried from an Admin
 * or Operator.
 * It does not provide any special functionality.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
