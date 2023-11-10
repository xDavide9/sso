package com.xdavide9.sso.authentication;

import com.xdavide9.sso.authentication.api.AuthenticationController;

/**
 * This record models a http request that it is sent when trying to sign up to the application.
 * This process is handled by an appropriate endpoint defined in {@link AuthenticationController}
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public record SignupRequest(String username, String email, String password) {
    /**
     * canonical constructor
     * @param username username
     * @param email email
     * @param password password
     */
    public SignupRequest {
        if (username == null || email == null || password == null)
            throw new IllegalStateException("username, email and password must be correctly set when creating a SignupRequest");
    }
}
