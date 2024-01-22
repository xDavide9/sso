package com.xdavide9.sso.exception.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

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
            case BAN -> error = "Cannot ban user";
            case UNBAN -> error = "Cannot unban user";
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
            case BAN -> error = "Cannot ban user";
            case UNBAN -> error = "Cannot unban user";
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

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Wrong input in the request");
        responseBody.put("message", "Make sure the request is formatted correctly");
        responseBody.put("status", BAD_REQUEST.toString());
        return new ResponseEntity<>(responseBody, BAD_REQUEST);
    }
}
