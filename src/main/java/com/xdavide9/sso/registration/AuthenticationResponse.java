package com.xdavide9.sso.registration;

public class AuthenticationResponse {

    private String token;

    private AuthenticationResponse(String token) {
        this.token = token;
    }

    // BUILDER

    public static class Builder {
        private String token;

        public Builder token(String token) {
            this.token = token;
            return this;
        }
        public AuthenticationResponse build() {
            return new AuthenticationResponse(token);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // GETTERS

    public String getToken() {
        return token;
    }
}
