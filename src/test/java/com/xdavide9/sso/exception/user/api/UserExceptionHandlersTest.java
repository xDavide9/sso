package com.xdavide9.sso.exception.user.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.*;

// unit test UserExceptionHandlers
class UserExceptionHandlersTest {

    private UserExceptionHandlers underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserExceptionHandlers();
    }

    @ParameterizedTest
    @CsvSource({
            "INFORMATION,Cannot get information about user",
            "BAN,Cannot ban user",
            "UNBAN,Cannot unban user",
            "PROMOTION,Cannot promote user",
            "DEMOTION,Cannot demote user",
            "TIMEOUT,Cannot time out user"
    })
    void itShouldHandleUserNotFoundException(String reason, String error) {
        // given
        UserNotFoundException e = new UserNotFoundException("ab", UserExceptionReason.valueOf(reason));
        // when
        ResponseEntity<?> response = underTest.handleUserNotFoundException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo(error);
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(NOT_FOUND.toString());
    }

    @ParameterizedTest
    @CsvSource({
            "INFORMATION,Cannot get information about user",
            "BAN,Cannot ban user",
            "UNBAN,Cannot unban user",
            "PROMOTION,Cannot promote user",
            "DEMOTION,Cannot demote user",
            "TIMEOUT,Cannot time out user"
    })
    void itShouldHandleUserCannotBeModifiedException(String reason, String error) {
        // given
        UserCannotBeModifiedException e = new UserCannotBeModifiedException("ab", UserExceptionReason.valueOf(reason));
        // when
        ResponseEntity<?> response = underTest.handleUserCannotBeModifiedException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo(error);
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(CONFLICT.toString());
    }

    @Test
    void itShouldHandleUserBannedException() {
        // given
        UserBannedException e = new UserBannedException("ab");
        // when
        ResponseEntity<?> response = underTest.handleUserBannedException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo("Login request into banned account");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(FORBIDDEN.toString());
    }
}