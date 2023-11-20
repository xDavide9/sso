package com.xdavide9.sso.jwt;

import com.xdavide9.sso.exception.jwt.MissingTokenException;
import com.xdavide9.sso.user.User;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetailsService;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

// unit test for JwtAuthenticationFilter

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @InjectMocks
    @Spy
    private JwtAuthenticationFilter underTest;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    private MockHttpServletRequest request;

    private MockHttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private SecurityContext securityContext;

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
        given(userDetailsService.loadUserByUsername(username)).willReturn(user);
        String token = "validToken";
        request.addHeader("Authorization", format("Bearer %s", token));
        given(jwtService.extractUsername(token)).willReturn(username);
        given(jwtService.isTokenValid(token, user)).willReturn(true);
        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
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
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void itShouldRespondWithUnauthorisedIfTheTokenIsMissing() {
        // given
        request.addHeader("Authorization", "invalid token");
        // when & then
        assertThatThrownBy(() -> underTest.doFilterInternal(request, response, filterChain))
                .isInstanceOf(MissingTokenException.class)
                .hasMessageContaining(format("Request [%s] must contain a jwt token", request));
    }

    @Test
    void itShouldDoNothingIfUserIsAlreadyAuthenticated() throws Exception {
        // given
        String username = "xdavide9";
        User user = new User();
        user.setUsername(username);
        String token = "validToken";
        request.addHeader("Authorization", format("Bearer %s", token));
        given(jwtService.extractUsername(token)).willReturn(username);
        given(underTest.securityContext()).willReturn(securityContext);
        given(securityContext.getAuthentication())
                .willReturn(new TestingAuthenticationToken(user, ""));  // not null
        // when
        underTest.doFilterInternal(request, response, filterChain);
        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }
}