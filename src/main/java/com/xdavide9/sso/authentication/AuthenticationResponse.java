package com.xdavide9.sso.authentication;

import com.xdavide9.sso.authentication.api.AuthenticationController;

/**
 * This class models a http response that is meant to be used when working with authentication
 * endpoints such as the ones defined in {@link AuthenticationController}. It uses the {@link Builder} pattern
 * to create immutable objects.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class AuthenticationResponse {

    /**
     * The jwt access token
     * @since 0.0.1-SNAPSHOT
     */
    private final String token;

    /**
     * private constructor used by the builder
     * @param token jwt token
     * @since 0.0.1-SNAPSHOT
     */
    private AuthenticationResponse(String token) {
        this.token = token;
    }

    // BUILDER

    /**
     * The builder for {@link AuthenticationResponse}
     * @since 0.0.1-SNAPSHOT
     * @author xdavide9
     */
    public static class Builder {

        /**
         * jwt token
         * @since 0.0.1-SNAPSHOT
         */
        private String token;

        /**
         * empty constructor
         * @since 0.0.1-SNAPSHOT
         */
        public Builder() {}

        /**
         * setter for token field
         * @param token the token to set
         * @return instance of the builder
         * @since 0.0.1-SNAPSHOT
         */
        public Builder token(String token) {
            this.token = token;
            return this;
        }

        /**
         * constructs a {@link AuthenticationResponse} from the builder
         * @return authentication response instance
         * @since 0.0.1-SNAPSHOT
         */
        public AuthenticationResponse build() {
            return new AuthenticationResponse(token);
        }
    }

    /**
     * returns a builder instance
     * @return builder instance
     * @since 0.0.1-SNAPSHOT
     */
    public static Builder builder() {
        return new Builder();
    }

    // GETTERS

    /**
     * getter for token field
     * @return jwt token
     * @since 0.0.1-SNAPSHOT
     */
    public String getToken() {
        return token;
    }
}
