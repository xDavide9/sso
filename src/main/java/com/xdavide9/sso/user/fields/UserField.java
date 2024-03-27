package com.xdavide9.sso.user.fields;

import com.xdavide9.sso.user.User;

/**
 * This enum keeps track of all the fields of {@link User}
 */
public enum UserField {
    USERNAME,
    EMAIL,
    /**
     * Used to track how many password changes the user has done (not the actual values)
     */
    PASSWORD,
    PHONE_NUMBER,
    FIRST_NAME,
    LAST_NAME,
    GENDER,
    COUNTRY,
    ROLE,
    DATE_OF_BIRTH,
    ENABLED,
    ACCOUNT_NON_EXPIRED,
    ACCOUNT_NON_LOCKED,
    DISABLED_UNTIL, CREDENTIALS_NON_EXPIRED
}
