package com.xdavide9.sso.exception.authentication;

import com.xdavide9.sso.exception.authentication.api.EmailTakenException;
import com.xdavide9.sso.exception.authentication.api.IncorrectPasswordException;
import com.xdavide9.sso.exception.authentication.api.PhoneNumberTakenException;
import com.xdavide9.sso.exception.authentication.api.UsernameTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.*;

// unit tests  AuthenticationExceptionHandlers
class AuthenticationExceptionsHandlerTest {

    private AuthenticationExceptionHandlers underTest;

    @BeforeEach
    void setUp() {
        underTest = new AuthenticationExceptionHandlers();
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
        assertThat(responseBody).isNotNull();
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
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo("Username already taken");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(CONFLICT.toString());
    }

    @Test
    void itShouldHandlePhoneNumberTakenException() {
        // given
        PhoneNumberTakenException e = new PhoneNumberTakenException("This phone number is already taken");
        // when
        ResponseEntity<?> response = underTest.handlePhoneNumberTakenException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo("Phone number already taken");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(CONFLICT.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldHandleSubjectNotFoundException() {
        // given
        SubjectNotFoundException e = new SubjectNotFoundException("Username [user] not found");
        // when
        ResponseEntity<?> response = underTest.handleSubjectNotFoundException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo("Subject (username/email) not found");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(NOT_FOUND.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldHandleIncorrectPasswordException() {
        // given
        IncorrectPasswordException e = new IncorrectPasswordException("Incorrect password");
        // when
        ResponseEntity<?> response = underTest.handleIncorrectPasswordException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo("incorrect input password at login");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(UNAUTHORIZED.toString());
    }
}