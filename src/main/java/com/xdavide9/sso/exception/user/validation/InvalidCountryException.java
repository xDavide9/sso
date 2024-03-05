package com.xdavide9.sso.exception.user.validation;

import com.xdavide9.sso.util.ValidatorService;

/**
 * This exception is thrown when {@link ValidatorService} finds an invalid country.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class InvalidCountryException extends RuntimeException {
    public InvalidCountryException(String message) {
        super(message);
    }
}
