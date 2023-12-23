package com.xdavide9.sso.authentication;

import com.xdavide9.sso.authentication.api.AuthenticationController;

/**
 * This record models a http request that it is sent when trying to log in the application.
 * This process is handled by an appropriate endpoint defined in {@link AuthenticationController}.
 * @since 0.0.1-SNAPSHOT
 * @param subject can be either username or email (user input)
 * @param password password
 * @author xdavide9
 */
public record LoginRequest(String subject, String password) {
    /**
     * Canonical constructor that checks that neither the subject nor the password is null. If any of the
     * two happen to be null, the class has been instantiated incorrectly.
     * @param subject username or email sent by the client
     * @param password password sent by the client
     */
    public LoginRequest {
        if (subject == null || password == null)
            throw new IllegalStateException("subject and password must be correctly set when creating a LoginRequest");
    }
}