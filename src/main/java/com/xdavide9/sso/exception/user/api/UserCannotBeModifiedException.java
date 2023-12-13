package com.xdavide9.sso.exception.user.api;

/**
 * This class is a custom runtime exception that is thrown when a user cannot be modified after
 * having been queried. In addition to an error message its constructor requires
 * a {@link UserExceptionReason} which specifies the reason why someone was trying to query for a user.
 * This is to let handlers return appropriate responses to clients.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
public class UserCannotBeModifiedException extends RuntimeException {

    private final UserExceptionReason reason;

    public UserCannotBeModifiedException(String message, UserExceptionReason reason) {
        super(message);
        this.reason = reason;
    }

    public UserExceptionReason getReason() {
        return reason;
    }
}
