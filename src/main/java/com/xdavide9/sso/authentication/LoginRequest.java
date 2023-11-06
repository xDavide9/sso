package com.xdavide9.sso.authentication;

import com.xdavide9.sso.authentication.api.AuthenticationController;

/**
 * This class models a http request that it is sent when trying to log in the application.
 * This process is handled by an appropriate endpoint defined in {@link AuthenticationController}
 * It uses the {@link AuthenticationResponse.Builder} pattern
 * to create immutable objects.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class LoginRequest {

    /**
     * This string either holds the username or the email
     * of the user that is trying to log in the application. Both of them
     * can be used to log in, and it doesn't matter which one is used as long
     * as there is a match in the database
     * @since 0.0.1-SNAPSHOT
     */
    private final String usernameOrEmail;
    /**
     * The password of the user that is trying to log in their account
     * @since 0.0.1-SNAPSHOT
     */
    private final String password;

    /**
     * private constructor to be used by the builder
     * @param usernameOrEmail either the username or the email of the user
     * @param password password
     * @since 0.0.1-SNAPSHOT
     */
    private LoginRequest(
            String usernameOrEmail,
            String password
    ) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    // BUILDER

    /**
     * The builder for {@link LoginRequest}
     * @since 0.0.1-SNAPSHOT
     */
    public static class Builder {
        /**
         * username or email of the user
         * since 0.0.1-SNAPSHOT
         */
        private String usernameOrEmail;
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
         * setter method for either the username or email
         * @param usernameOrEmail username or email
         * @return instance of the builder
         * @since 0.0.1-SNAPSHOT
         */
        public Builder usernameOrEmail(String usernameOrEmail) {
            this.usernameOrEmail = usernameOrEmail;
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
         * returns an instance of {@link LoginRequest} made from the builder
         * @since 0.0.1-SNAPSHOT
         * @return login request instance
         */
        public LoginRequest build() {
            return new LoginRequest(
                    usernameOrEmail,
                    password
            );
        }
    }

    /**
     * returns an instance of the builder
     * @return instance of the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    // GETTERS

    /**
     * getter for usernameOrEmail
     * @return usernameOrEmail
     */
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    /**
     * getter for password
     * @return password
     */
    public String getPassword() {
        return password;
    }
}

