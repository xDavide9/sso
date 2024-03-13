package com.xdavide9.sso.exception.user.change;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class UserChangeExceptionsHandlerTest {

    private UserChangeExceptionsHandler underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserChangeExceptionsHandler();
    }

    @Test
    void itShouldHandleUserChangeNotFoundException() {
        // given
        UserChangeNotFoundException e = new UserChangeNotFoundException("ab");
        // when
        ResponseEntity<?> response = underTest.handleUserChangeNotFoundException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo("User change record not found");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(NOT_FOUND.toString());
    }
}