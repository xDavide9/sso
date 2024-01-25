package com.xdavide9.sso.exception.user.api;

import com.xdavide9.sso.user.User;

/**
 * This runtime exception is thrown when someone tries to log in into
 * a banned account (enabled field set to false in {@link User}.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class UserBannedException extends RuntimeException {
    public UserBannedException(String message) {
        super(message);
    }
}
