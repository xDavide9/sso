package com.xdavide9.sso.jwt;

import com.xdavide9.sso.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

// unit test for JwtTokenValidatorFilter

@ExtendWith(MockitoExtension.class)
class JwtTokenValidatorFilterTest {

    @InjectMocks
    private JwtTokenValidatorFilter underTest;
    @Mock
    private JwtService jwtService;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void itShouldDoFilterInternalCorrectlyWithValidToken() throws Exception {
        // given
        String username = "xdavide9";
        User user = new User();
        user.setUsername(username);
        String token = "validToken";
        request.addHeader("Authorization", format("Bearer %s", token));
        given(jwtService.extractUsername(token)).willReturn(username);
        // when
        underTest.doFilterInternal(request, response, filterChain);
        // then
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(request.getAttribute("username")).isEqualTo(username);
        assertThat(request.getAttribute("token")).isEqualTo(token);
    }

    @Test
    void itShouldNotContinueFilterChainMissingToken() throws Exception {
        // given
        // when
        underTest.doFilterInternal(request, response, filterChain);
        // then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).isEqualTo("Missing jwt Token. Every request should include " +
                "a valid jwt token to authenticate to the server.");
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    void itShouldNotContinueFilterChainExpiredToken() throws Exception {
        // given
        String token = "expiredToken";
        request.addHeader("Authorization", format("Bearer %s", token));
        given(jwtService.extractUsername(token)).willThrow(new ExpiredJwtException(null, null, "Token expired"));
        // when
        underTest.doFilterInternal(request, response, filterChain);
        // then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).isEqualTo("Expired jwt token. Login again to provide a new one.");
        verify(filterChain, times(0)).doFilter(request, response);
    }
}