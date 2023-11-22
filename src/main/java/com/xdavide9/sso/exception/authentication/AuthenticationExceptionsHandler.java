package com.xdavide9.sso.exception.authentication;

import com.xdavide9.sso.exception.authentication.api.EmailTakenException;
import com.xdavide9.sso.exception.authentication.api.PasswordTooShortException;
import com.xdavide9.sso.exception.authentication.api.UsernameTakenException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ControllerAdvice
public class AuthenticationExceptionsHandler {

    @ExceptionHandler(value = EmailTakenException.class)
    public ResponseEntity<?> handleEmailTakenException(EmailTakenException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Email already taken");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", UNAUTHORIZED);
        return new ResponseEntity<>(responseBody, UNAUTHORIZED);
    }

    @ExceptionHandler(value = UsernameTakenException.class)
    public ResponseEntity<?> handleUsernameTakenException(UsernameTakenException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Username already taken");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", UNAUTHORIZED);
        return new ResponseEntity<>(responseBody, UNAUTHORIZED);
    }

    @ExceptionHandler(value = PasswordTooShortException.class)
    public ResponseEntity<?> handlePasswordTooShortException(PasswordTooShortException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Input password it too short (< 8 characters)");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", UNAUTHORIZED);
        return new ResponseEntity<>(responseBody, UNAUTHORIZED);
    }
}
