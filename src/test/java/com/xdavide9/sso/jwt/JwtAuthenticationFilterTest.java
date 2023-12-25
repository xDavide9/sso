package com.xdavide9.sso.jwt;

import com.xdavide9.sso.user.User;
import jakarta.servlet.FilterChain;
import org.assertj.core.api.AssertionsForClassTypes;
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
import org.springframework.test.context.ActiveProfiles;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class JwtAuthenticationFilterTest {

    @InjectMocks
    @Spy
    private JwtAuthenticationFilter underTest;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private FilterChain filterChain;
    @Captor
    private ArgumentCaptor<Authentication> captor;
    private MockHttpServletRequest request;

    private MockHttpServletResponse response;
    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }
    @Test
    void itShouldRegisterUserToSecurityContextCorrectly() throws Exception {
        // given
        String username = "xdavide9";
        User user = new User();
        user.setUsername(username);
        given(userDetailsService.loadUserByUsername(username)).willReturn(user);
        String token = "validToken";
        request.addHeader("Authorization", format("Bearer %s", token));
        given(jwtService.extractUsername(token)).willReturn(username);
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
}