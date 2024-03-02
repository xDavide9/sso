package com.xdavide9.sso.exception.user.validation;

import com.xdavide9.sso.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * This Exception Handler class handles exceptions related to the process of validating
 * {@link User} fields. It does so by catching appropriate exceptions and returning responses to clients
 */
@ControllerAdvice
public class UserValidationExceptionHandler {

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
}
