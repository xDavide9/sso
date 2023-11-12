package com.xdavide9.sso.authentication;

import com.xdavide9.sso.authentication.api.AuthenticationController;

/**
 * This record models a http response that is meant to be used when working with authentication
 * endpoints such as the ones defined in {@link AuthenticationController}.
 * @since 0.0.1-SNAPSHOT
 * @param token jwtToken
 * @author xdavide9
 */
public record AuthenticationResponse(String token) {
    /**
     * canonical constructor
     * @param token token
     * @since 0.0.1-SNAPSHOT
     */
    public AuthenticationResponse {
        if (token == null)
            throw new IllegalStateException("Token cannot be null when creating an Authentication Response");
    }
}
