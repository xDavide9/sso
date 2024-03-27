package com.xdavide9.sso.exception.user.validation;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

class UserValidationExceptionHandlersTest {

    private UserValidationExceptionHandlers underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserValidationExceptionHandlers();
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

    @Test
    void itShouldHandleInvalidUsernameException() {
        // given
        InvalidUsernameException e = new InvalidUsernameException("ab");
        // when
        ResponseEntity<?> response = underTest.handleInvalidUsernameException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo("Invalid username");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(BAD_REQUEST.toString());
    }

    @Test
    void itShouldHandleInvalidDateOfBirthException() {
        // given
        InvalidDateOfBirthException e = new InvalidDateOfBirthException("ab");
        // when
        ResponseEntity<?> response = underTest.handleInvalidDateOfBirthException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo("Invalid date of birth");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(BAD_REQUEST.toString());
    }

    @Test
    void itShouldHandleInvalidCountryException() {
        // given
        InvalidCountryException e = new InvalidCountryException("ab");
        // when
        ResponseEntity<?> response = underTest.handleInvalidCountryException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo("Invalid country");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(BAD_REQUEST.toString());
    }

    @Test
    void itShouldHandlePersistenceException() {
        // given
        PersistenceException e = new PersistenceException("ab");
        // when
        ResponseEntity<?> response = underTest.handlePersistenceException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo("Persistence error");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(CONFLICT.toString());
    }

    @Test
    void itShouldHandleDataIntegrityViolationException() {
        // given
        DataIntegrityViolationException e = new DataIntegrityViolationException("ab");
        // when
        ResponseEntity<?> response = underTest.handleDataIntegrityViolationException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("error")).isEqualTo("Data integrity violation error");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(CONFLICT.toString());
    }
}
