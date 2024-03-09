package com.xdavide9.sso.exception.authentication.api;

/**
 * This exception is thrown when:
 * 1) a new user tries to register a new account
 * 2) an operator or admin changes the email field of a user
 * but the input email they provide is already taken
 * 3) a user tries to change their email
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class EmailTakenException extends RuntimeException{
    public EmailTakenException(String message) {
        super(message);
    }
}
