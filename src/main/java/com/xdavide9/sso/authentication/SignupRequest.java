package com.xdavide9.sso.authentication;

import com.xdavide9.sso.authentication.api.AuthenticationController;

/**
 * This class models a http request that it is sent when trying to sign up to the application.
 * This process is handled by an appropriate endpoint defined in {@link AuthenticationController}
 * It uses the {@link AuthenticationResponse.Builder} pattern
 * to create immutable objects.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class SignupRequest {

    /**
     * username
     * @since 0.0.1-SNAPSHOT
     */
    private final String username;

    /**
     * email
     * @since 0.0.1-SNAPSHOT
     */
    private final String email;
    /**
     * password
     * @since 0.0.1-SNAPSHOT
     */
    private final String password;

    /**
     * private constructor to be used by the builder
     * @param username username
     * @param email email
     * @param password password
     */
    private SignupRequest(
            String username,
            String email,
            String password
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // BUILDER

    /**
     * The builder for {@link SignupRequest}
     * @since 0.0.1-SNAPSHOT
     * @author xdavide9
     */
    public static class Builder {

        /**
         * username
         * @since 0.0.1-SNAPSHOT
         */
        private String username;
        /**
         * email
         * @since 0.0.1-SNAPSHOT
         */
        private String email;
        /**
         * password
         * @since 0.0.1-SNAPSHOT
         */
        private String password;

        /**
         * empty constructor
         * @since 0.0.1-SNAPSHOT
         */
        public Builder() {}

        /**
         * setter method for the username
         * @since 0.0.1-SNAPSHOT
         * @param username username
         * @return instance of the builder
         */
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        /**
         * setter method for the email
         * @since 0.0.1-SNAPSHOT
         * @param email email
         * @return instance of the builder
         */
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * setter method for the password
         * @since 0.0.1-SNAPSHOT
         * @param password password
         * @return instance of the builder
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * returns an instance of {@link SignupRequest} made from the builder.
         * @return instance of SignupRequest
         * @since 0.0.1-SNAPSHOT
         */
        public SignupRequest build() {
            return new SignupRequest(
                    username,
                    email,
                    password
            );
        }
    }

    /**
     * returns an instance of the builder
     * @since 0.0.1-SNAPSHOT
     * @return instance of the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    // GETTERS

    /**
     * getter
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * getter
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * getter
     * @return password
     */
    public String getPassword() {
        return password;
    }
}
