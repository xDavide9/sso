package com.xdavide9.sso.exception.authentication;

import com.xdavide9.sso.exception.authentication.api.EmailTakenException;
import com.xdavide9.sso.exception.authentication.api.PasswordTooShortException;
import com.xdavide9.sso.exception.authentication.api.UsernameTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.*;

// unit tests  AuthenticationExceptionHandler
@ActiveProfiles("test")
class AuthenticationExceptionsHandlerTest {

    private AuthenticationExceptionsHandler underTest;

    @BeforeEach
    void setUp() {
        underTest = new AuthenticationExceptionsHandler();
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldHandleEmailTakenException() {
        // given
        EmailTakenException e = new EmailTakenException("This email is already taken");
        // when
        ResponseEntity<?> response = underTest.handleEmailTakenException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assert responseBody != null;
        assertThat(responseBody.get("error")).isEqualTo("Email already taken");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(CONFLICT.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldHandleUsernameTakenException() {
        // given
        UsernameTakenException e = new UsernameTakenException("This username is already taken");
        // when
        ResponseEntity<?> response = underTest.handleUsernameTakenException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assert responseBody != null;
        assertThat(responseBody.get("error")).isEqualTo("Username already taken");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(CONFLICT.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldHandlePasswordTooShortException() {
        // given
        PasswordTooShortException e = new PasswordTooShortException("This password is too short");
        // when
        ResponseEntity<?> response = underTest.handlePasswordTooShortException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assert responseBody != null;
        assertThat(responseBody.get("error")).isEqualTo("Input password is too short (< 8 characters)");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(BAD_REQUEST.toString());
    }
}