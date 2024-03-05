package com.xdavide9.sso.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.xdavide9.sso.exception.authentication.api.EmailTakenException;
import com.xdavide9.sso.exception.authentication.api.UsernameTakenException;
import com.xdavide9.sso.exception.user.validation.*;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.user.fields.PasswordDTO;
import com.xdavide9.sso.user.fields.country.Country;
import com.xdavide9.sso.user.fields.country.CountryRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.apache.commons.validator.routines.EmailValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidatorServiceTest {

    @InjectMocks
    @Spy
    private ValidatorService underTest;

    @Mock
    private Validator jakartaValidator;

    @Mock
    private PhoneNumberUtil phoneNumberUtil;

    @Mock
    private CountryRepository countryRepository;
    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<PasswordDTO> dtoCaptor;

    @Mock
    private EmailValidator emailValidator;

    @Test
    void itShouldValidatePasswordNoViolations() {
        // given
        String password = "ValidPassword!";
        given(jakartaValidator.validate(any(PasswordDTO.class))).willReturn(new HashSet<>());
        // when
        underTest.validateRawPassword(password);
        // then
        verify(underTest).validateUsingJakartaConstraints(dtoCaptor.capture());
        assertThat(dtoCaptor.getValue().getPassword()).isEqualTo(password);
    }

    @Test
    void itShouldValidatePasswordThrowException() {
        // given
        String password = "ValidPassword!";
        ConstraintViolation<PasswordDTO> violation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<PasswordDTO>> violations = new HashSet<>();
        violations.add(violation);
        given(jakartaValidator.validate(any(PasswordDTO.class))).willReturn(violations);
        // when & then
        assertThatThrownBy(() -> underTest.validateRawPassword(password))
                .isInstanceOf(ConstraintViolationException.class)
                .hasFieldOrPropertyWithValue("constraintViolations", violations);
    }

    @Test
    void itShouldValidatePhoneNumberCorrectly() throws Exception {
        // given
        String phoneNumber = "123";
        Phonenumber.PhoneNumber parsed = new Phonenumber.PhoneNumber();
        given(phoneNumberUtil.parse(phoneNumber, null)).willReturn(parsed);
        given(phoneNumberUtil.isValidNumber(parsed)).willReturn(true);
        // when & then
        assertThatCode(() -> underTest.validatePhoneNumber(phoneNumber)).doesNotThrowAnyException();
        }

    @Test
    void itShouldNotValidatePhoneNumberCouldNotParse() throws Exception {
        // given
        String phoneNumber = "123";
        NumberParseException numberParseException = mock(NumberParseException.class);
        given(numberParseException.getMessage()).willReturn("message");
        given(phoneNumberUtil.parse(phoneNumber, null)).willThrow(numberParseException);
        // when & then
        assertThatThrownBy(() -> underTest.validatePhoneNumber(phoneNumber))
                .isInstanceOf(InvalidPhoneNumberException.class)
                .hasMessageContaining(numberParseException.getMessage())
                .hasCause(numberParseException);
    }

    @Test
    void itShouldNotValidatePhoneNumberInvalidParsedPhoneNumber() throws Exception {
        // given
        String phoneNumber = "123";
        Phonenumber.PhoneNumber parsed = new Phonenumber.PhoneNumber();
        given(phoneNumberUtil.parse(phoneNumber, null)).willReturn(parsed);
        given(phoneNumberUtil.isValidNumber(parsed)).willReturn(false);
        // when & then
        assertThatThrownBy(() -> underTest.validatePhoneNumber(phoneNumber))
                .isInstanceOf(InvalidPhoneNumberException.class)
                .hasMessageContaining(format("Parsed phone number [%s] is invalid", parsed));
    }

    @Test
    void itShouldValidateValidEmail() {
        // given
        String email = "validEmail";
        given(underTest.getEmailValidator()).willReturn(emailValidator);
        given(userRepository.existsByEmail(email)).willReturn(false);
        given(emailValidator.isValid(email)).willReturn(true);
        // when & then
        assertThatCode(() -> underTest.validateEmail(email)).doesNotThrowAnyException();
    }

    @Test
    void itShouldValidateInvalidEmailAndThrow() {
        // given
        String email = "invalidEmail";
        given(underTest.getEmailValidator()).willReturn(emailValidator);
        given(emailValidator.isValid(email)).willReturn(false);
        // when & then
        assertThatThrownBy(() -> underTest.validateEmail(email))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining(format("Email [%s] is not valid, provide a new one", email));
    }

    @Test
    void itShouldValidateTakenEmailAndThrow() {
        // given
        String email = "takenEmail";
        given(underTest.getEmailValidator()).willReturn(emailValidator);
        given(emailValidator.isValid(email)).willReturn(true);
        given(userRepository.existsByEmail(email)).willReturn(true);
        // when & then
        assertThatThrownBy(() -> underTest.validateEmail(email))
                .isInstanceOf(EmailTakenException.class)
                .hasMessageContaining("Email [takenEmail] is already taken");
    }

    @Test
    void itShouldValidateValidUsername() {
        assertThatCode(() -> underTest.validateUsername("valid")).doesNotThrowAnyException();;
    }

    @Test
    void itShouldValidateInvalidUsername() {
        // given
        String username = null;
        // when & then
        assertThatThrownBy(() ->underTest.validateUsername(username))
                .isInstanceOf(InvalidUsernameException.class)
                .hasMessageContaining(format("Username [%s] is not valid, provide a new one", username));
    }

    @Test
    void itShouldValidateTakenEmail() {
        // given
        String username = "takenUsername";
        given(userRepository.existsByUsername(username)).willReturn(true);
        // when & then
        assertThatThrownBy(() -> underTest.validateUsername(username))
                .isInstanceOf(UsernameTakenException.class)
                .hasMessageContaining("Username [takenUsername] is already taken");
    }

    @Test
    void itShouldValidateCountryCorrectly() {
        // given
        Country country = new Country("IT", "Italy", 39);
        given(countryRepository.existsByCountryCodeAndDisplayNameAndPhoneNumberCode(
                country.getCountryCode(), country.getDisplayName(), country.getPhoneNumberCode()
        )).willReturn(true);
        // when & then
        assertThatCode(() -> underTest.validateCountry(country)).doesNotThrowAnyException();
    }

    @Test
    void itShouldValidateInvalidCountry() {
        // given
        Country country = new Country("I", "Italy", 39);
        given(countryRepository.existsByCountryCodeAndDisplayNameAndPhoneNumberCode(
                country.getCountryCode(), country.getDisplayName(), country.getPhoneNumberCode()
        )).willReturn(false);
        // when & then
        assertThatThrownBy(() -> underTest.validateCountry(country))
                .isInstanceOf(InvalidCountryException.class)
                .hasMessageContaining(format("Country [%s] is not valid, provide a new one", country));
    }

    @Test
    void itShouldValidateDateOfBirthCorrectly() {
        // given
        LocalDate dateOfBirth = LocalDate.ofYearDay(2000, 50);
        // when & then
        assertThatCode(() -> underTest.validateDateOfBirth(dateOfBirth)).doesNotThrowAnyException();
    }

    @Test
    void itShouldValidateInvalidFutureDateOfBirth() {
        // given
        LocalDate dateOfBirth = LocalDate.ofYearDay(3000, 50);
        // when & then
        assertThatThrownBy(() -> underTest.validateDateOfBirth(dateOfBirth))
                .isInstanceOf(InvalidDateOfBirthException.class)
                .hasMessageContaining(format("Date of birth [%s] is not valid, provide a new one", dateOfBirth));
    }
}