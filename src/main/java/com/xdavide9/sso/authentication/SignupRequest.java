package com.xdavide9.sso.authentication;

import com.xdavide9.sso.authentication.api.AuthenticationController;

/**
 * This record models a http request that it is sent when trying to sign up to the application.
 * This process is handled by an appropriate endpoint defined in {@link AuthenticationController}
 * @param username username sent by client
 * @param email email sent by client
 * @param password password sent by client
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public record SignupRequest(String username, String email, String password) {
    /**
     * canonical constructor that checks that any of the field is not null. If any happens to be null,
     * the class has been instantiated incorrectly
     * @param username username sent by the client
     * @param email email sent by the client
     * @param password password sent by the client
     * @since 0.0.1-SNAPSHOT
     */
    public SignupRequest {
        if (username == null || email == null || password == null)
            throw new IllegalStateException("username, email and password must be correctly set when creating a SignupRequest");
    }
}
