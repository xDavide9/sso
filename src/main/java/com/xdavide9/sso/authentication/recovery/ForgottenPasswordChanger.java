package com.xdavide9.sso.authentication.recovery;

import com.xdavide9.sso.user.User;
import org.springframework.http.ResponseEntity;

/**
 * The process of changing a forgotten password consists of 2 phases:
 * 1) authenticating the user in a way that is different from logging in
 * 2) changing the password of their account if the authentication was successful
 * The password must be changed because not even the server knows it for security reasons
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public interface ForgottenPasswordChanger {
    /**
     * Implement some way to authenticate a user without using login
     * @return currently authenticated user
     */
    User authenticateUser();

    /**
     * Modify the password of the authenticated user and
     * inform whether the change was successful
     */
    ResponseEntity<?> changePassword(User user, String newPassword);
}
