package com.xdavide9.sso.authentication;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// unit test for the builder of the model

class AuthenticationResponseTest {

    @Test
    void itShouldGenerateResponseCorrectly() {
        // given
        String token = "token123";
        // when
        AuthenticationResponse response = AuthenticationResponse
                .builder()
                .token(token)
                .build();
        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(token);
    }
}