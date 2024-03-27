package com.xdavide9.sso.exception.user.api;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

/**
 * This class holds methods annotated with {@link ExceptionHandler}
 * that handle exceptions related to the User api. Some of these exceptions are defined by
 * the application while others are already defined by other libraries but are handled here.
 * Each {@link ExceptionHandler} returns an appropriate http response to clients.
 * {@link UserNotFoundException} and {@link UserCannotBeModifiedException} are more general purpose exceptions,
 * therefore, in order to clarify the specific situation in which they are thrown, a special enum constant
 * {@link UserExceptionReason} is passed to them, and they are handled accordingly to this value.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@ControllerAdvice
public class UserExceptionHandlers {

    // DEFINED BY ME

    /**
     * Helper method for handleUserNotFoundException and handleUserCannotBeModifiedException
     */
    private String getErrorString(UserExceptionReason reason) {
        String error;
        switch (reason) {
            case INFORMATION -> error = "Cannot get information about user";
            case BAN -> error = "Cannot ban user";
            case UNBAN -> error = "Cannot unban user";
            case DEMOTION -> error = "Cannot demote user";
            case PROMOTION -> error = "Cannot promote user";
            case TIMEOUT -> error = "Cannot time out user";
            default -> error = "User not found";
        }
        return error;
    }
    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", getErrorString(e.getReason()));
        responseBody.put("message", e.getMessage());
        responseBody.put("status", NOT_FOUND.toString());
        return new ResponseEntity<>(responseBody, NOT_FOUND);
    }

    @ExceptionHandler(value = UserCannotBeModifiedException.class)
    public ResponseEntity<Map<String, Object>> handleUserCannotBeModifiedException(UserCannotBeModifiedException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", getErrorString(e.getReason()));
        responseBody.put("message", e.getMessage());
        responseBody.put("status", CONFLICT.toString());
        return new ResponseEntity<>(responseBody, CONFLICT);
    }

    @ExceptionHandler(value = UserBannedException.class)
    public ResponseEntity<Map<String, Object>> handleUserBannedException(UserBannedException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Login request into banned account");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", FORBIDDEN.toString());
        return new ResponseEntity<>(responseBody, FORBIDDEN);
    }

    // ALREADY DEFINED BY OTHER LIBRARIES

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Wrong input in the request");
        responseBody.put("message", "Make sure the request is formatted correctly");
        responseBody.put("status", BAD_REQUEST.toString());
        return new ResponseEntity<>(responseBody, BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Invalid user input");
        responseBody.put("message", "One or more constraints have been violated, provide valid input next time");
        List<String> violations = e.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();
        responseBody.put("violations", violations);
        responseBody.put("status", BAD_REQUEST.toString());
        return new ResponseEntity<>(responseBody, BAD_REQUEST);
    }
}
