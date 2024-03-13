package com.xdavide9.sso.util;

import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.user.change.UserChangeRepository;
import com.xdavide9.sso.user.fields.Gender;
import com.xdavide9.sso.user.fields.country.Country;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserModifierServiceTest {

    // TODO update this class with new changes

    @InjectMocks
    private UserModifierService underTest;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ValidatorService validatorService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserChangeRepository userChangeRepository;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void itShouldSetUsernameCorrectly() {
        // given
        User user = new User();
        String username = "username";
        // when
        underTest.setUsername(user, username);
        // then
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        verify(validatorService).validateUsername(username);
        assertThat(captured.getUsername()).isEqualTo(username);
    }

    @Test
    void itShouldSetEmailCorrectly() {
        // given
        User user = new User();
        String email = "emailgmail.com";
        // when
        underTest.setEmail(user, email);
        // then
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        verify(validatorService).validateEmail(email);
        assertThat(captured.getEmail()).isEqualTo(email);
    }

    @Test
    void itShouldSetPasswordCorrectly() {
        // given
        User user = new User();
        String password = "ValidPass1!";
        given(passwordEncoder.encode(password)).willReturn("encoded");
        // when
        underTest.setPassword(user, password);
        // then
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        verify(validatorService).validateRawPassword(password);
        assertThat(captured.getPassword()).isEqualTo("encoded");
    }

    @Test
    void itShouldSetPhoneNumberCorrectly() {
        // given
        User user = new User();
        String phoneNumber = "+393339977000";
        // when
        underTest.setPhoneNumber(user, phoneNumber);
        // then
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        verify(validatorService).validatePhoneNumber(phoneNumber);
        assertThat(captured.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    void itShouldSetFirstNameCorrectly() {
        // given
        User user = new User();
        String firstName = "John";
        // when
        underTest.setFirstName(user, firstName);
        // then
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        assertThat(captured.getFirstName()).isEqualTo(firstName);
    }

    @Test
    void itShouldSetLastNameCorrectly() {
        // given
        User user = new User();
        String lastName = "Smith";
        // when
        underTest.setLastName(user, lastName);
        // then
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        assertThat(captured.getLastName()).isEqualTo(lastName);
    }

    @Test
    void itShouldSetCountryCorrectly() {
        // given
        User user = new User();
        Country country = new Country("IT", "Italy", 39);
        // when
        underTest.setCountry(user, country);
        // then
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        verify(validatorService).validateCountry(country);
        assertThat(captured.getCountry()).isEqualTo(country);
    }

    @Test
    void itShouldSetDateOfBirthCorrectly() {
        // given
        User user = new User();
        LocalDate dateOfBirth = LocalDate.ofYearDay(2000, 50);
        // when
        underTest.setDateOfBirth(user, dateOfBirth);
        // then
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        verify(validatorService).validateDateOfBirth(dateOfBirth);
        assertThat(captured.getDateOfBirth()).isEqualTo(dateOfBirth);
    }

    @Test
    void itShouldSetGenderCorrectly() {
        // given
        User user = new User();
        Gender gender = Gender.MALE;
        // when
        underTest.setGender(user, gender);
        // then
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        assertThat(captured.getGender()).isEqualTo(gender);
    }
}