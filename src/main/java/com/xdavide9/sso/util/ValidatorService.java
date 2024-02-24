package com.xdavide9.sso.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.xdavide9.sso.exception.user.api.InvalidPhoneNumberException;
import com.xdavide9.sso.user.PasswordDTO;
import com.xdavide9.sso.user.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    public ValidatorService(Validator jakartaValidator) {
        this.jakartaValidator = jakartaValidator;
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
     * Validates input user by applying jakarta constraints defined in {@link User} class.
     */
    public void validateUser(User user) {
        validateUsingJakartaConstraints(user);
    }

    /**
     * Validates input password by applying jakarta constraints defined in {@link PasswordDTO} class.
     */
    public void validateRawPassword(String password) {
        PasswordDTO dto = new PasswordDTO(password);
        validateUsingJakartaConstraints(dto);
    }

    // TODO use google's libphonenumber

    /**
     * Validates input using google's libphonenumber.
     */
    public void validatePhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneNumberUtil = getPhoneNumberUtil();
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, null);
            if (!phoneNumberUtil.isValidNumber(parsedNumber))
                throw new InvalidPhoneNumberException(format("Parsed phone number [%s] is invalid", parsedNumber));
        } catch (NumberParseException e) {
            throw new InvalidPhoneNumberException(e.getMessage(), e);
        }
    }

    /**
     * Wrapping for testability
     */
    public PhoneNumberUtil getPhoneNumberUtil() {
        return PhoneNumberUtil.getInstance();
    }

    // TODO use apache commons

    /**
     * Validates using apache commons' email validator
     */
    public void validateEmail(String email) {

    }

    public void validateUsername(String username) {
    }
}
