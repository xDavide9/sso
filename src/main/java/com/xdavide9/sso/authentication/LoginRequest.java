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
public record LoginRequest(String subject, String password) {}