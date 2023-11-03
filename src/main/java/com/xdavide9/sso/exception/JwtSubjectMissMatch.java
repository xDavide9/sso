package com.xdavide9.sso.exception;


/**
 * This class is a custom runtime exception that is thrown when a jwt token is not valid.
 * An invalid jwt token is most likely a token that does not contain the same {@link com.xdavide9.sso.user.User}
 * as the original one that was sent along with the token. This token is most likely not expired because otherwise a
 * {@link io.jsonwebtoken.ExpiredJwtException} would have been thrown first instead.
 * It does not provide any special functionality.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
public class JwtSubjectMissMatch extends RuntimeException {
    /**
     * empty constructor
     * @since 0.0.1-SNAPSHOT
     */
    public JwtSubjectMissMatch() {
    }

    /**
     * super constructor
     * @param message message
     * @since 0.0.1-SNAPSHOT
     */
    public JwtSubjectMissMatch(String message) {
        super(message);
    }

    /**
     * super constructor
     * @param message message
     * @param cause cause
     * @since 0.0.1-SNAPSHOT
     */
    public JwtSubjectMissMatch(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * super constructor
     * @param cause cause
     * @since 0.0.1-SNAPSHOT
     */
    public JwtSubjectMissMatch(Throwable cause) {
        super(cause);
    }

    /**
     * super constructor
     * @param cause cause
     * @param message message
     * @param enableSuppression enableSuppression
     * @param writableStackTrace writableStackTrace
     * @since 0.0.1-SNAPSHOT
     */
    public JwtSubjectMissMatch(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

