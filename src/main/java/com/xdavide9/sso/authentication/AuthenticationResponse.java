package com.xdavide9.sso.authentication;

import com.xdavide9.sso.authentication.api.AuthenticationController;
import com.xdavide9.sso.jwt.JwtService;

/**
 * This record models a http response that is meant to be used when working with authentication
 * endpoints such as the ones defined in {@link AuthenticationController}.
 * @since 0.0.1-SNAPSHOT
 * @param token jwtToken to be created with {@link JwtService}
 * @author xdavide9
 */
public record AuthenticationResponse(String token) {
    /**
     * Canonical constructor that checks if the provided token is null. If it happens to be null the
     * class has been instantiated incorrectly
     * @param token token to be created with {@link JwtService}
     */
    public AuthenticationResponse {
        if (token == null)
            throw new IllegalStateException("Token cannot be null when creating an Authentication Response");
    }
}
