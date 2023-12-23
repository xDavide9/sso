package com.xdavide9.sso.exception.user.api;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * This class holds methods annotated with {@link ExceptionHandler}
 * that handle exceptions related to the User api by returning appropriate
 * http responses to clients.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@ControllerAdvice
public class UserExceptionsHandler {
    @ExceptionHandler(value = UserNotFoundException.class)
    public void handleUserNotFoundException() {
        // TODO handle user not found exception
    }
}
