package com.xdavide9.sso.exception.user.api;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * This class is the Exception Handler for exception related to users' api.
 * Each method is annotated with {@link ExceptionHandler} and handles the list of exception specified
 * as the value attribute. This design allows to clearly map any exception to an action that should be performed
 * as a result of that exception. Each of these actions is described in the name of the method.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@ControllerAdvice
public class UserExceptionsHandler {

    /**
     * empty constructor
     * @since 0.0.1-SNAPSHOT
     */
    public UserExceptionsHandler() {}

    /**
     * Exception Handler
     * @since 0.0.1-SNAPSHOT
     */
    @ExceptionHandler(value = UserNotFoundException.class)
    public void handleUserNotFoundException() {
        // TODO handle user not found exception
    }
}
