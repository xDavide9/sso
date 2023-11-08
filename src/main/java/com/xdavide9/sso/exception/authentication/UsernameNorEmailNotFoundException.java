package com.xdavide9.sso.exception.authentication;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Custom exception that extends directly {@link UsernameNotFoundException} which is meant
 * to be thrown only in a {@link UserDetailsService} implementation
 * when a user by username or email is not found
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
public class UsernameNorEmailNotFoundException extends UsernameNotFoundException {

    /**
     * super constructor
     * @since 0.0.1-SNAPSHOT
     * @param msg message
     */
    public UsernameNorEmailNotFoundException(String msg) {
        super(msg);
    }

    /**
     * super constructor
     * @since 0.0.1-SNAPSHOT
     * @param msg message
     * @param cause cause
     */
    public UsernameNorEmailNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
