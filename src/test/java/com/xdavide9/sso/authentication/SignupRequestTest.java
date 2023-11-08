package com.xdavide9.sso.authentication;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// unit test for the builder of the model

class SignupRequestTest {
    @Test
    void itShouldGenerateSignupRequestCorrectly() {
        // given
        String username = "xdavide9";
        String password = "password123";
        String email = "email@email.com";
        // when
        SignupRequest signupRequest = SignupRequest.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
        // then
        assertThat(signupRequest).isNotNull();
        assertThat(signupRequest.getEmail()).isEqualTo(email);
        assertThat(signupRequest.getUsername()).isEqualTo(username);
        assertThat(signupRequest.getPassword()).isEqualTo(password);
    }
}