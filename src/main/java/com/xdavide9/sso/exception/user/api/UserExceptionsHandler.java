package com.xdavide9.sso.exception.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * This class holds methods annotated with {@link ExceptionHandler}
 * that handle exceptions related to the User api by returning appropriate
 * http responses to clients. A special enum constant {@link UserExceptionReason} is passed to
 * these exceptions in order to further customize the responses.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@ControllerAdvice
public class UserExceptionsHandler {
    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException e) {
        String error;
        switch (e.getReason()) {
            case INFORMATION -> error = "Cannot get information about user";
            case DELETION -> error = "Cannot delete user";
            case DEMOTION -> error = "Cannot demote user";
            case PROMOTION -> error = "Cannot promote user";
            default -> error = "User not found";
        }
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", error);
        responseBody.put("message", e.getMessage());
        responseBody.put("status", NOT_FOUND.toString());
        return new ResponseEntity<>(responseBody, NOT_FOUND);
    }

    @ExceptionHandler(value = UserCannotBeModifiedException.class)
    public ResponseEntity<Map<String, Object>> handleUserCannotBeModifiedException(UserCannotBeModifiedException e) {
        String error;
        switch (e.getReason()) {
            case INFORMATION -> error = "Cannot get information about user";
            case DELETION -> error = "Cannot delete user";
            case DEMOTION -> error = "Cannot demote user";
            case PROMOTION -> error = "Cannot promote user";
            default -> error = "User cannot be modified";
        }
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", error);
        responseBody.put("message", e.getMessage());
        responseBody.put("status", CONFLICT.toString());
        return new ResponseEntity<>(responseBody, CONFLICT);
    }
}
