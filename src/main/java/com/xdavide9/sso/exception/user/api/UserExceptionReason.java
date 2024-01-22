package com.xdavide9.sso.exception.user.api;

/**
 * This enum specifies what is the reason a user was queried by an Admin or Operator.
 * This is passed to {@link UserNotFoundException} and {@link UserCannotBeModifiedException}
 * to let ExceptionHandlers construct an appropriate response in each failing case.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public enum UserExceptionReason {
    /**
     * The user was queried to simply retrieve their details from the database by either an Admin or Operator
     */
    INFORMATION,
    /**
     * The user was queried from an Admin to be promoted to Operator
     */
    PROMOTION,
    /**
     * The user was queried from an Admin to be demoted to a plain User from Operator role.
     */
    DEMOTION,
    /**
     * The user was queried from an Admin to be banned from the system
     */
    BAN,
    /**
     * The user was queried from an Admin to unbanned from the system
     */
    UNBAN
}
