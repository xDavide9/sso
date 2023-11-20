package com.xdavide9.sso.exception.jwt;

/**
 * This exception is thrown when someone sends a request to a protected resource
 * without providing a jwt token (not an invalid one, just no token at all).
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class MissingTokenException extends RuntimeException {
    public MissingTokenException(String message) {
        super(message);
    }
}
