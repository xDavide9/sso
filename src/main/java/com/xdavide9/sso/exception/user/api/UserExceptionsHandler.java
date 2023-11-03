package com.xdavide9.sso.exception.user.api;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionsHandler {

    @ExceptionHandler(value = UserNotFoundException.class)
    public void handleUserNotFoundException() {
        // TODO handle user not found exception
    }
}
