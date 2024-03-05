package com.xdavide9.sso.authentication.api;

// unit test for AuthenticationService

import com.xdavide9.sso.authentication.AuthenticationResponse;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.authentication.SignupRequest;
import com.xdavide9.sso.exception.authentication.api.EmailTakenException;
import com.xdavide9.sso.exception.authentication.api.IncorrectPasswordException;
import com.xdavide9.sso.exception.authentication.api.UsernameTakenException;
import com.xdavide9.sso.exception.user.api.UserBannedException;
import com.xdavide9.sso.jwt.JwtService;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.user.fields.Gender;
import com.xdavide9.sso.user.fields.country.Country;
import com.xdavide9.sso.util.ValidatorService;
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

import java.time.LocalDate;
import java.util.Objects;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    // do not user TestAuthenticator as its functionality is being tested

    @InjectMocks
    private AuthenticationService underTest;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ValidatorService validatorService;
    @Mock
    private UserDetailsService userDetailsService;
    @Captor
    private ArgumentCaptor<User> userCaptor;


    // signup

    @Test
    void itShouldSignUpNewUserCorrectlyWithRequiredFields() {
        // given
        String username = "xdavide9";
        String password = "password";
        String email = "email@sso.com";
        given(jwtService.generateToken(any(User.class))).willReturn("token123");
        given(passwordEncoder.encode(password)).willReturn("encodedPassword");
        SignupRequest request = new SignupRequest(username, email, password);
        // when
        ResponseEntity<AuthenticationResponse> response = underTest.signup(request);
        // then
        verify(repository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        verify(validatorService).validateRawPassword(password);
        verify(validatorService).validateUsername(username);
        verify(validatorService).validateEmail(email);
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
    void itShouldSignUpNewUserCorrectlyWithAllFields() {
        // given
        String username = "xdavide9";
        String password = "password";
        String email = "email@sso.com";
        String firstName = "John";
        String lastName = "Smith";
        String phoneNumber = "+393337799000";
        Gender gender = Gender.MALE;
        LocalDate dateOfBirth = LocalDate.ofYearDay(2000, 50);
        Country country = new Country("IT", "Italy", 39);
        given(jwtService.generateToken(any(User.class))).willReturn("token123");
        given(passwordEncoder.encode(password)).willReturn("encodedPassword");
        SignupRequest request = new SignupRequest(username, email, password);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setPhoneNumber(phoneNumber);
        request.setGender(gender);
        request.setDateOfBirth(dateOfBirth);
        request.setCountry(country);
        // when
        ResponseEntity<AuthenticationResponse> response = underTest.signup(request);
        // then
        verify(repository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        verify(validatorService).validateRawPassword(password);
        verify(validatorService).validateUsername(username);
        verify(validatorService).validateEmail(email);
        assertThat(capturedUser.getUsername()).isEqualTo(username);
        assertThat(capturedUser.getEmail()).isEqualTo(email);
        assertThat(capturedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(capturedUser.getFirstName()).isEqualTo(firstName);
        assertThat(capturedUser.getLastName()).isEqualTo(lastName);
        assertThat(capturedUser.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(capturedUser.getGender()).isEqualTo(gender);
        assertThat(capturedUser.getCountry()).isEqualTo(country);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).token())
                .isEqualTo("token123");
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