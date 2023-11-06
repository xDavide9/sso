package com.xdavide9.sso.exception.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

/**
 * This class is the Exception Handler for exception related to jwt tokens.
 * Each method is annotated with {@link ExceptionHandler} and handles the list of exception specified
 * as the value attribute. This design allows to clearly map any exception to an action that should be performed
 * as a result of that exception. Each of these actions is described in the name of the method.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@ControllerAdvice
public class JwtExceptionsHandler {

    /**
     * empty constructor
     * @since 0.0.1-SNAPSHOT
     */
    public JwtExceptionsHandler() {}

    /**
     * Exception handler
     * @since 0.0.1-SNAPSHOT
     */
    @ExceptionHandler(value = {JwtSubjectMissMatchException.class, SignatureException.class})
    public void rotateSecurityKey() {
        // TODO implement securityKey rotation
    }

    /**
     * Exception handler
     * @since 0.0.1-SNAPSHOT
     * @return redirect
     */
    @ExceptionHandler(value = ExpiredJwtException.class)
    public RedirectView redirectToLoginPage() {
        return new RedirectView("/login?error=expired");
    }
}
