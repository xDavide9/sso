package com.xdavide9.sso.exception.user.validation;

import com.xdavide9.sso.util.ValidatorService;

/**
 * This exception is thrown when {@link ValidatorService} finds an invalid dateOfBirth.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class InvalidDateOfBirthException extends RuntimeException {
    public InvalidDateOfBirthException(String message) {
        super(message);
    }

}
