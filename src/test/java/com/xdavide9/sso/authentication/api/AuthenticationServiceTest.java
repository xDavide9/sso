package com.xdavide9.sso.authentication.api;

// unit test for AuthenticationService

import com.xdavide9.sso.authentication.AuthenticationResponse;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.authentication.SignupRequest;
import com.xdavide9.sso.exception.authentication.api.EmailTakenException;
import com.xdavide9.sso.exception.authentication.api.IncorrectPasswordException;
import com.xdavide9.sso.exception.authentication.api.PasswordTooShortException;
import com.xdavide9.sso.exception.authentication.api.UsernameTakenException;
import com.xdavide9.sso.exception.user.api.UserBannedException;
import com.xdavide9.sso.jwt.JwtService;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.util.UserValidatorService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService underTest;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserValidatorService validatorService;

    @Mock
    private UserDetailsService userDetailsService;
    @Captor
    private ArgumentCaptor<User> captor;

    // signup

    @Test
    void itShouldSignUpNewUserCorrectly() {
        // given
        String username = "xdavide9";
        String password = "password"; // >= 8 characters
        String email = "email@sso.com";
        given(repository.existsByEmail(email)).willReturn(false);
        given(repository.existsByUsername(username)).willReturn(false);
        given(jwtService.generateToken(any(User.class))).willReturn("token123");
        given(passwordEncoder.encode(password)).willReturn("encodedPassword");
        SignupRequest request = new SignupRequest(username, email, password);
        // when
        ResponseEntity<AuthenticationResponse> response = underTest.signup(request);
        // then
        verify(validatorService).validate(captor.capture());
        User capturedUser = captor.getValue();
        verify(repository).save(capturedUser);
        assertThat(capturedUser.getUsername()).isEqualTo(username);
        assertThat(capturedUser.getEmail()).isEqualTo(email);
        assertThat(capturedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).token())
                .isEqualTo("token123");
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(jwtService);
        verifyNoMoreInteractions(validatorService);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void itShouldNotSignupUsernameTakenAndThrow() {
        // given
        String username = "xdavide9";
        String password = "password"; // >= 8 characters
        String email = "email@sso.com";
        given(repository.existsByUsername(username)).willReturn(true);
        SignupRequest request = new SignupRequest(username, email, password);
        // when & then
        assertThatThrownBy(() -> underTest.signup(request))
                .isInstanceOf(UsernameTakenException.class)
                .hasMessageContaining(format("Username [%s] is already taken", username));
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(validatorService);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void itShouldNotSignupEmailTakenAndThrow() {
        // given
        String username = "xdavide9";
        String password = "password"; // >= 8 characters
        String email = "email@sso.com";
        given(repository.existsByUsername(username)).willReturn(false);
        given(repository.existsByEmail(email)).willReturn(true);
        SignupRequest request = new SignupRequest(username, email, password);
        // when & then
        assertThatThrownBy(() -> underTest.signup(request))
                .isInstanceOf(EmailTakenException.class)
                .hasMessageContaining(format("Email [%s] is already taken", email));
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(validatorService);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void itShouldNotSignupPasswordTooShortAndThrow() {
        // given
        String username = "xdavide9";
        String password = "short"; // < 8 characters
        String email = "email@sso.com";
        given(repository.existsByUsername(username)).willReturn(false);
        given(repository.existsByEmail(email)).willReturn(false);
        SignupRequest request = new SignupRequest(username, email, password);
        // when & then
        assertThatThrownBy(() -> underTest.signup(request))
                .isInstanceOf(PasswordTooShortException.class)
                .hasMessageContaining("Password must be at least 8 characters long");
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(validatorService);
        verifyNoInteractions(passwordEncoder);
    }

    // login

    @Test
    void itShouldLoginCorrectly() {
        // given
        String username = "xdavide9@email.com";
        String email = "email@email.com";
        String rawPassword = "rawPassword";
        String encodedPassword = "encodedPassword";
        User user = new User(username, email, encodedPassword); // assume user is from database where password is encoded
        LoginRequest request = new LoginRequest(username, rawPassword);
        String token = "token123";
        given(userDetailsService.loadUserByUsername(username)).willReturn(user);
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);
        given(jwtService.generateToken(user)).willReturn(token);
        // when
        ResponseEntity<AuthenticationResponse> response = underTest.login(request);
        // then
        assertThat(Objects.requireNonNull(response.getBody()).token()).isEqualTo(token);
    }

    @Test
    void itShouldNotLoginAccountIsBanned() {
        // given
        String username = "username";
        String email = "email@email.com";
        String rawPassword = "rawPassword";
        String encodedPassword = "encodedPassword";
        User user = new User(username, email, encodedPassword);
        user.setEnabled(false);
        LoginRequest request = new LoginRequest(username, rawPassword);
        given(userDetailsService.loadUserByUsername(username)).willReturn(user);
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);
        // when & then
        assertThatThrownBy(() -> underTest.login(request))
                .isInstanceOf(UserBannedException.class)
                .hasMessageContaining(format("The account with subject [%s] is banned.", username));
    }

    @Test
    void itShouldNotLoginRawAndEncodedPasswordDoNotMatch() {
        // given
        String username = "xdavide9@email.com";
        String email = "email@email.com";
        String rawPassword = "rawPassword";
        String encodedPassword = "encodedPassword";
        User user = new User(username, email, encodedPassword); // assume user is from database where password is encoded
        LoginRequest request = new LoginRequest(username, rawPassword);
        given(userDetailsService.loadUserByUsername(username)).willReturn(user);
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(false);
        // when & then
        assertThatThrownBy(() -> underTest.login(request))
                .isInstanceOf(IncorrectPasswordException.class)
                .hasMessageContaining(format("Incorrect input password at login for subject [%s]", username));
    }
}