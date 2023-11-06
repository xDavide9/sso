package com.xdavide9.sso.authentication;

public class SignupRequest {

    private String username, email, password;

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

    public static class Builder {
        private String username, email, password;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public SignupRequest build() {
            return new SignupRequest(
                    username,
                    email,
                    password
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // GETTERS

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
