package com.xdavide9.sso.exception.user.fields.country;

/**
 * This exception is thrown when a country is not found
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(String message) {
        super(message);
    }
}
