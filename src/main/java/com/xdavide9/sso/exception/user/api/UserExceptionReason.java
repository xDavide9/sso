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
     * The user was queried to simply retrieve their details from the database
     * @since 0.0.1-SNAPSHOT
     */
    INFORMATION,
    /**
     * The user was queried from an Admin to be promoted to Operator
     * @since 0.0.1-SNAPSHOT
     */
    PROMOTION,
    /**
     * The user was queried from an Admin to be demoted to a plain User from Operator role.
     * @since 0.0.1-SNAPSHOT
     */
    DEMOTION,
    /**
     * The user was queried from an Admin to be cancelled from the system irreversibly
     * @since 0.0.1-SNAPSHOT
     */
    DELETION
}
