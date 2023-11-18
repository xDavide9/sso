package com.xdavide9.sso.exception.jwt;

import com.xdavide9.sso.user.User;
import io.jsonwebtoken.ExpiredJwtException;


/**
 * This class is a custom runtime exception that is thrown when a jwt token is not valid.
 * An invalid jwt token is most likely a token that does not contain the same {@link User}
 * as the original one that was sent along with the token. This token is most likely not expired because otherwise a
 * {@link ExpiredJwtException} would have been thrown first instead.
 * It does not provide any special functionality.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
public class JwtSubjectMissMatchException extends RuntimeException {
    public JwtSubjectMissMatchException(String message) {
        super(message);
    }
}

