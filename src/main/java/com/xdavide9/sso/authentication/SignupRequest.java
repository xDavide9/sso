package com.xdavide9.sso.authentication;

import com.xdavide9.sso.authentication.api.AuthenticationController;
import com.xdavide9.sso.user.PasswordDTO;

/**
 * This record models a http request that it is sent when trying to sign up to the application.
 * This process is handled by an appropriate endpoint defined in {@link AuthenticationController}
 * @param username username sent by client
 * @param email email sent by client
 * @param passwordDTO password sent by client
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public record SignupRequest(String username, String email, PasswordDTO passwordDTO) {}
