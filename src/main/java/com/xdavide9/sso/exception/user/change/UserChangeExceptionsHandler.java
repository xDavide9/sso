package com.xdavide9.sso.exception.user.change;

import com.xdavide9.sso.user.change.UserChange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * This class contains methods marked with {@link ExceptionHandler}.
 * They handle exceptions related to {@link UserChange}
 */
@ControllerAdvice
public class UserChangeExceptionsHandler {

    @ExceptionHandler(value = UserChangeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserChangeNotFoundException(UserChangeNotFoundException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "User change record not found");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", NOT_FOUND.toString());
        return new ResponseEntity<>(responseBody, NOT_FOUND);
    }
}
