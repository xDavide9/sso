package com.xdavide9.sso.exception.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class JwtExceptionsHandler {
    @ExceptionHandler(value = {JwtSubjectMissMatchException.class, SignatureException.class})
    public void rotateSecurityKey() {
        // TODO implement securityKey rotation
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    public RedirectView redirectToLoginPage() {
        return new RedirectView("/login?error=expired");
    }
}
