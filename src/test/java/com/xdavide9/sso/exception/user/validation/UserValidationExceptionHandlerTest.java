package com.xdavide9.sso.exception.user.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class UserValidationExceptionHandlerTest {

    private UserValidationExceptionHandler underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserValidationExceptionHandler();
    }

    @Test
    void itShouldHandleInvalidPhoneNumberException() {
        // given
        InvalidPhoneNumberException e = new InvalidPhoneNumberException("ab");
        // when
        ResponseEntity<?> response = underTest.handleInvalidPhoneNumberException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo("Invalid phone number");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(BAD_REQUEST.toString());
    }

    @Test
    void itShouldHandleInvalidEmailException() {
        // given
        InvalidEmailException e = new InvalidEmailException("ab");
        // when
        ResponseEntity<?> response = underTest.handleInvalidEmailException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo("Invalid email");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(BAD_REQUEST.toString());
    }
}
