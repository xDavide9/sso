package com.xdavide9.sso.authentication;

import com.xdavide9.sso.authentication.api.AuthenticationController;

/**
 * This record models a http request that it is sent when trying to log in the application.
 * This process is handled by an appropriate endpoint defined in {@link AuthenticationController}
 * @since 0.0.1-SNAPSHOT
 * @param usernameOrEmail usernameOrEmail (user input)
 * @param password password
 * @author xdavide9
 */
public record LoginRequest(String usernameOrEmail, String password) {
    /**
     * canonical constructor
     * @param usernameOrEmail username or email (user input)
     * @param password password
     * @since 0.0.1-SNAPSHOT
     */
    public LoginRequest {
        if (usernameOrEmail == null || password == null)
            throw new IllegalStateException("usernameOrEmail and password must be correctly set when creating a LoginRequest");
    }
}