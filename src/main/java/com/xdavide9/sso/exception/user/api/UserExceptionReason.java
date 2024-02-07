package com.xdavide9.sso.exception.user.api;

/**
 * This enum specifies what is the reason a specific exception related to user is thrown. The name of the exception
 * generally describes what is the problem with the given situation while this value should allow
 * to target the exact problem so that it can be handled in the best way.
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
    UNBAN,
    /**
     * The user was queried by an Admin or Operator to be timed out
     */
    TIMEOUT
}
