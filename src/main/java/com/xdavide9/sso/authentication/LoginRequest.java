package com.xdavide9.sso.authentication;

public class LoginRequest {

    private String usernameOrEmail, password;

    private LoginRequest(
            String usernameOrEmail,
            String password
    ) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    // BUILDER

    public static class Builder {
        private String usernameOrEmail, password;

        public Builder usernameOrEmail(String usernameOrEmail) {
            this.usernameOrEmail = usernameOrEmail;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public LoginRequest build() {
            return new LoginRequest(
                    usernameOrEmail,
                    password
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // GETTERS

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }
}

