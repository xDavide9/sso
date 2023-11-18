package com.xdavide9.sso.exception.authentication.api;

/**
 * This exception is thrown when a user tries to register an account but the email
 * they provide is already taken
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class EmailTakenException extends RuntimeException{
    public EmailTakenException(String message) {
        super(message);
    }
}
