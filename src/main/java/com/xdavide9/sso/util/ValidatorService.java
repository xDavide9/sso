package com.xdavide9.sso.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.xdavide9.sso.exception.authentication.api.EmailTakenException;
import com.xdavide9.sso.exception.authentication.api.UsernameTakenException;
import com.xdavide9.sso.exception.user.validation.*;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.user.fields.PasswordDTO;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.fields.country.Country;
import com.xdavide9.sso.user.fields.country.CountryRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

import static java.lang.String.format;

/**
 * This is the main validation service for every form of user input in the application. It makes use
 * of jakarta standard constraints for fields like {@link User} and its raw password (not visible directly), and
 * external libraries for email, phone number. There is a generics method to implement more validation using
 * jakarta constraints easily.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
@Service
public class ValidatorService {

    // TODO implement checks for offensive language using google's perspective api

    private final Validator jakartaValidator;

    private final PhoneNumberUtil phoneNumberUtil;

    private final CountryRepository countryRepository;
    private final UserRepository userRepository;



    @Autowired
    public ValidatorService(Validator jakartaValidator,
                            PhoneNumberUtil phoneNumberUtil,
                            CountryRepository countryRepository,
                            UserRepository userRepository) {
        this.jakartaValidator = jakartaValidator;
        this.phoneNumberUtil = phoneNumberUtil;
        this.countryRepository = countryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Validates generic t object with jakarta constraints defined in T's class.
     * @param t object to be validated
     * @param <T> class that contains constraints
     */
    public <T> void validateUsingJakartaConstraints(T t) {
        Set<ConstraintViolation<T>> violations = jakartaValidator.validate(t);
        if (!violations.isEmpty())
            throw new ConstraintViolationException(violations);
    }


    /**
     * Validates input password by applying jakarta constraints defined in {@link PasswordDTO} class.
     */
    public void validateRawPassword(String password) {
        PasswordDTO dto = new PasswordDTO(password);
        validateUsingJakartaConstraints(dto);
    }

    /**
     * Validates input using google's libphonenumber, and that it doesn't already exist
     */
    public void validatePhoneNumber(String phoneNumber) {
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, null);
            if (!phoneNumberUtil.isValidNumber(parsedNumber))
                throw new InvalidPhoneNumberException(format("Parsed phone number [%s] is invalid", parsedNumber));
        } catch (NumberParseException e) {
            throw new InvalidPhoneNumberException(e.getMessage(), e);
        }
    }

    /**
     * Validates using apache commons' email validator, and that it doesn't already exist
     */
    public void validateEmail(String email) {
        if (!getEmailValidator().isValid(email))
            throw new InvalidEmailException(format("Email [%s] is not valid, provide a new one", email));
        if (userRepository.existsByEmail(email)) {
            throw new EmailTakenException(
                    format("Email [%s] is already taken", email)
            );
        }
    }

    /**
     * Wrapping for testability
     */
    public EmailValidator getEmailValidator() {
        return EmailValidator.getInstance();
    }

    /**
     * Validates username by checking that is not null nor blank, and that it doesn't already exist
     */
    public void validateUsername(String username) {
        if (username == null || username.isBlank())
            throw new InvalidUsernameException(format("Username [%s] is not valid, provide a new one", username));
        if (userRepository.existsByUsername(username)) {
            throw new UsernameTakenException(
                    format("Username [%s] is already taken", username)
            );
        }
    }

    public void validateCountry(Country country) {
        if (!countryRepository.existsByCountryCodeAndDisplayNameAndPhoneNumberCode(
                country.getCountryCode(), country.getDisplayName(), country.getPhoneNumberCode()
        )) throw new InvalidCountryException(format("Country [%s] is not valid, provide a new one", country));
    }

    public void validateDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth.isAfter(LocalDate.now()))
            throw new InvalidDateOfBirthException(format("Date of birth [%s] is not valid, provide a new one", dateOfBirth));
    }
}
