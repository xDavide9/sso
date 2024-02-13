package com.xdavide9.sso.exception.authentication;

import com.xdavide9.sso.exception.authentication.api.EmailTakenException;
import com.xdavide9.sso.exception.authentication.api.IncorrectPasswordException;
import com.xdavide9.sso.exception.authentication.api.UsernameTakenException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

/**
 * This class holds methods annotated with {@link ExceptionHandler}
 * that handle exceptions related to the Authentication process by returning appropriate
 * http responses to clients.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@ControllerAdvice
public class AuthenticationExceptionsHandler {

    @ExceptionHandler(value = EmailTakenException.class)
    public ResponseEntity<Map<String, Object>> handleEmailTakenException(EmailTakenException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Email already taken");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", CONFLICT.toString());
        return new ResponseEntity<>(responseBody, CONFLICT);
    }

    @ExceptionHandler(value = UsernameTakenException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameTakenException(UsernameTakenException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Username already taken");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", CONFLICT.toString());
        return new ResponseEntity<>(responseBody, CONFLICT);
    }

    @ExceptionHandler(value = SubjectNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSubjectNotFoundException(SubjectNotFoundException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Subject (username/email) not found");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", NOT_FOUND.toString());
        return new ResponseEntity<>(responseBody, NOT_FOUND);
    }

    @ExceptionHandler(value = IncorrectPasswordException.class)
    public ResponseEntity<Map<String, Object>> handleIncorrectPasswordException(IncorrectPasswordException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "incorrect input password at login");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", UNAUTHORIZED.toString());
        return new ResponseEntity<>(responseBody, UNAUTHORIZED);
    }
}
