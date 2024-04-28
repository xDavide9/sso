package com.xdavide9.sso.jwt;

import com.xdavide9.sso.authentication.verification.EmailVerifier;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter underTest;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private FilterChain filterChain;
    @Mock
    private UserRepository repository;
    @Mock
    private EmailVerifier emailVerifier;
    private Clock clock;
    @Captor
    private ArgumentCaptor<Authentication> captor;
    private MockHttpServletRequest request;

    private MockHttpServletResponse response;
    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        JwtAuthenticationFilter originalFilter = new JwtAuthenticationFilter(userDetailsService, jwtService, clock, repository, emailVerifier);
        underTest = Mockito.spy(originalFilter);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }
    @Test
    void itShouldRegisterUserToSecurityContextCorrectly() throws Exception {
        // given
        String username = "xdavide9";
        User user = new User();
        user.setUsername(username);
        given(emailVerifier.isEmailVerified(user)).willReturn(true);
        given(userDetailsService.loadUserByUsername(username)).willReturn(user);
        String token = "validToken";
        request.setAttribute("token", token);
        request.setAttribute("username", username);
        given(jwtService.isTokenValid(token, user)).willReturn(true);
        given(underTest.securityContext()).willReturn(securityContext);
        // when
        underTest.doFilterInternal(request, response, filterChain);
        // then
        verify(securityContext).setAuthentication(captor.capture());
        Authentication capturedAuthentication = captor.getValue();
        assertThat(capturedAuthentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(capturedAuthentication.getPrincipal()).isEqualTo(user);
        assertThat(capturedAuthentication.getCredentials()).isNull();
        assertThat(capturedAuthentication.getAuthorities()).isEqualTo(user.getAuthorities());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void itShouldNotRegisterUserToSecurityContextInvalidToken() throws Exception {
        // given
        String username = "xdavide9";
        User user = new User();
        user.setUsername(username);
        given(userDetailsService.loadUserByUsername(username)).willReturn(user);
        String token = "validToken";
        request.setAttribute("token", token);
        request.setAttribute("username", username);
        given(jwtService.isTokenValid(token, user)).willReturn(false);
        given(underTest.securityContext()).willReturn(securityContext);
        // when
        underTest.doFilterInternal(request, response, filterChain);
        // then
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).isEqualTo("Invalid Jwt token. Login again.");
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    void itShouldNotRegisterUserToSecurityContextBecauseItIsDisabled() throws Exception {
        // given
        String username = "xdavide9";
        User user = new User();
        user.setUsername(username);
        user.setEnabled(false);
        user.setDisabledUntil(LocalDateTime.now(clock).plusHours(1));
        given(userDetailsService.loadUserByUsername(username)).willReturn(user);
        String token = "validToken";
        request.setAttribute("token", token);
        request.setAttribute("username", username);
        given(jwtService.isTokenValid(token, user)).willReturn(true);
        given(underTest.securityContext()).willReturn(securityContext);
        // when
        underTest.doFilterInternal(request, response, filterChain);
        // then
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).isEqualTo(format("This account [%s] has been disabled by an Admin.", user.getUuid()));
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    void itShouldReEnableUserAndRegisterToSecurityContext() throws Exception {
        // given
        String username = "xdavide9";
        User user = new User();
        given(emailVerifier.isEmailVerified(user)).willReturn(true);
        user.setUsername(username);
        user.setEnabled(false);
        user.setDisabledUntil(LocalDateTime.now(clock).minusHours(1));
        given(userDetailsService.loadUserByUsername(username)).willReturn(user);
        String token = "validToken";
        request.setAttribute("token", token);
        request.setAttribute("username", username);
        given(jwtService.isTokenValid(token, user)).willReturn(true);
        given(underTest.securityContext()).willReturn(securityContext);
        // when
        underTest.doFilterInternal(request, response, filterChain);
        // then
        verify(repository).save(user);
        verify(securityContext).setAuthentication(captor.capture());
        Authentication capturedAuthentication = captor.getValue();
        assertThat(capturedAuthentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        User capturedUser = (User) capturedAuthentication.getPrincipal();
        assertThat(capturedUser.isEnabled()).isTrue();
        assertThat(capturedAuthentication.getCredentials()).isNull();
        assertThat(capturedAuthentication.getAuthorities()).isEqualTo(user.getAuthorities());
        verify(filterChain).doFilter(request, response);
    }
}