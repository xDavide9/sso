package com.xdavide9.sso.exception.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ActiveProfiles("test")
class JwtExceptionsHandlerTest {

    private JwtExceptionsHandler underTest;

    @BeforeEach
    void setUp() {
        underTest = new JwtExceptionsHandler();
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldHandeExpiredJwtException() {
        // given
        ExpiredJwtException e = new ExpiredJwtException(null, null, "expired");
        // when
        ResponseEntity<?> response = underTest.handleExpiredJwtException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assert responseBody != null;
        assertThat(responseBody.get("error")).isEqualTo("Expired Jwt Token");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(UNAUTHORIZED);
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldHandleMissingTokenException() {
        // given
        MissingTokenException e = new MissingTokenException("missing");
        // when
        ResponseEntity<?> response = underTest.handleMissingTokenException(e);
        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assert responseBody != null;
        assertThat(responseBody.get("error")).isEqualTo("Missing Jwt Token");
        assertThat(responseBody.get("message")).isEqualTo(e.getMessage());
        assertThat(responseBody.get("status")).isEqualTo(UNAUTHORIZED);
    }
}