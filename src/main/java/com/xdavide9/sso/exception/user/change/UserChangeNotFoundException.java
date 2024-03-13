package com.xdavide9.sso.exception.user.change;

import com.xdavide9.sso.user.change.UserChange;

/**
 * This exception is thrown when a {@link UserChange} is not found.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class UserChangeNotFoundException extends RuntimeException{
    public UserChangeNotFoundException(String message) {
        super(message);
    }
}
