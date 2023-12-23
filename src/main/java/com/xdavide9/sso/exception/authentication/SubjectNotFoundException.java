package com.xdavide9.sso.exception.authentication;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Custom exception that extends directly {@link UsernameNotFoundException} which is meant
 * to be thrown only in a {@link UserDetailsService} implementation
 * when a user by username or email (subject) is not found.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
public class SubjectNotFoundException extends UsernameNotFoundException {
    public SubjectNotFoundException(String message) {
        super(message);
    }

}
