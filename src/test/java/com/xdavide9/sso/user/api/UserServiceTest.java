package com.xdavide9.sso.user.api;

// unit test for UserService

import com.xdavide9.sso.jwt.JwtService;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.util.UserModifierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    @Spy
    private UserService underTest;

    @Mock
    private UserModifierService userModifierService;

    private User principal;

    private final String token = "123";

    @Mock
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        principal = new User();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
    }

    @Test
    void itShouldGetPersonalDetails() {
        // given
        // when
        underTest.getPersonalDetails();
        // then
        verify(underTest).getPrincipal();
    }

    @Test
    void itShouldChangeUsername() {
        // given
        String username = "username";
        // when
        when(jwtService.generateToken(principal)).thenReturn(token);
        ResponseEntity<String> response = underTest.changeUsername(username);
        // then
        verify(userModifierService).setUsername(principal, username);
        assertThat(response.getBody()).isEqualTo(token);
    }

    @Test
    void itShouldChangeEmail() {
        // given
        String email = "email@email.com";
        // when
        when(jwtService.generateToken(principal)).thenReturn(token);
        ResponseEntity<String> response = underTest.changeEmail(email);
        // then
        verify(userModifierService).setEmail(principal, email);
        assertThat(response.getBody()).isEqualTo(token);
    }

    @Test
    void itShouldChangePassword() {
        // given
        String password = "VeryStrongPass1!";
        // when
        when(jwtService.generateToken(principal)).thenReturn(token);
        ResponseEntity<String> response = underTest.changePassword(password);
        // then
        verify(userModifierService).setPassword(principal, password);
        assertThat(response.getBody()).isEqualTo(token);
    }

    @Test
    void itShouldChangePhoneNumber() {
        // given
        String phoneNumber = "+393337799000";
        // when
        when(jwtService.generateToken(principal)).thenReturn(token);
        ResponseEntity<String> response = underTest.changePhoneNumber(phoneNumber);
        // then
        verify(userModifierService).setPhoneNumber(principal, phoneNumber);
        assertThat(response.getBody()).isEqualTo(token);
    }

    @Test
    void itShouldChangeCountry() {
        // given
        String countryCode = "IT";
        // when
        when(jwtService.generateToken(principal)).thenReturn(token);
        ResponseEntity<String> response = underTest.changeCountry(countryCode);
        // then
        verify(userModifierService).setCountry(principal, countryCode);
        assertThat(response.getBody()).isEqualTo(token);
    }
}