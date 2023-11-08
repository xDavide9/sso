package com.xdavide9.sso.authentication;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// unit test for builder of the model

class LoginRequestTest {
    @Test
    void itShouldGenerateLoginRequestCorrectly() {
        // given
        String usernameOrEmail = "usernameOrEmail";
        String password = "password123";
        // when
        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail(usernameOrEmail)
                .password(password)
                .build();
        // then
        assertThat(loginRequest).isNotNull();
        assertThat(loginRequest.getUsernameOrEmail()).isEqualTo(usernameOrEmail);
        assertThat(loginRequest.getPassword()).isEqualTo(password);
    }
}