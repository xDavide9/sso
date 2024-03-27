package com.xdavide9.sso.exception.user.validation;

import com.xdavide9.sso.user.User;
import jakarta.persistence.PersistenceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * This Exception Handler class handles exceptions related to the process of validating
 * {@link User} fields. It also handles the exception thrown when database constraints are violated
 * It does so by catching appropriate exceptions and returning responses to clients
 */
@ControllerAdvice
public class UserValidationExceptionHandlers {

    @ExceptionHandler(value = InvalidPhoneNumberException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPhoneNumberException(InvalidPhoneNumberException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Invalid phone number");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", BAD_REQUEST.toString());
        return new ResponseEntity<>(responseBody, BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidEmailException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidEmailException(InvalidEmailException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Invalid email");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", BAD_REQUEST.toString());
        return new ResponseEntity<>(responseBody, BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidUsernameException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUsernameException(InvalidUsernameException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Invalid username");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", BAD_REQUEST.toString());
        return new ResponseEntity<>(responseBody, BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidDateOfBirthException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDateOfBirthException(InvalidDateOfBirthException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Invalid date of birth");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", BAD_REQUEST.toString());
        return new ResponseEntity<>(responseBody, BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidCountryException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCountryException(InvalidCountryException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Invalid country");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", BAD_REQUEST.toString());
        return new ResponseEntity<>(responseBody, BAD_REQUEST);
    }

    // DB

    @ExceptionHandler(value = PersistenceException.class)
    public ResponseEntity<Map<String, Object>> handlePersistenceException(PersistenceException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Persistence error");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", CONFLICT.toString());
        return new ResponseEntity<>(responseBody, CONFLICT);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Data integrity violation error");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", CONFLICT.toString());
        return new ResponseEntity<>(responseBody, CONFLICT);
    }
}
