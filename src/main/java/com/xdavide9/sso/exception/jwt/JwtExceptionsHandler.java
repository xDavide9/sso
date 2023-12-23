package com.xdavide9.sso.exception.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * This class holds methods annotated with {@link ExceptionHandler}
 * that handle exceptions related to manipulating JWT tokens  by returning appropriate
 * http responses to clients. One method rotates the security key if there are reasons to think there
 * was a breach in the application.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@ControllerAdvice
public class JwtExceptionsHandler {
    /**
     * Exception handler
     * @since 0.0.1-SNAPSHOT
     */
    @ExceptionHandler(value = {JwtSubjectMissMatchException.class, SignatureException.class})
    public void rotateSecurityKey() {
        // TODO implement securityKey rotation
    }

    /**
     * Exception handler for {@link ExpiredJwtException}.
     * Returns a json response with details about what went wrong to clients.
     * The token provided with the request is expired.
     * @param e exception handled
     * @since 0.0.1-SNAPSHOT
     * @return redirect
     */
    @ExceptionHandler(value = ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Expired Jwt Token");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", UNAUTHORIZED);
        return new ResponseEntity<>(responseBody, UNAUTHORIZED);
    }

    /**
     * Exception handler for {@link MissingTokenException}.
     * Returns a json response with details about want when wrong to clients.
     * Every request to a secured resource must include a jwtToken that is later processed.
     * @since 0.0.1-SNAPSHOT
     * @return redirect
     */
    @ExceptionHandler(value = MissingTokenException.class)
    public ResponseEntity<?> handleMissingTokenException(MissingTokenException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Missing Jwt Token");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", UNAUTHORIZED);
        return new ResponseEntity<>(responseBody, UNAUTHORIZED);
    }
}
