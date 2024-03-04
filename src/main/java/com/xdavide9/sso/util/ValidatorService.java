package com.xdavide9.sso.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.xdavide9.sso.exception.user.validation.InvalidEmailException;
import com.xdavide9.sso.exception.user.validation.InvalidPhoneNumberException;
import com.xdavide9.sso.exception.user.validation.InvalidUsernameException;
import com.xdavide9.sso.user.fields.PasswordDTO;
import com.xdavide9.sso.user.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.apache.commons.validator.routines.EmailValidator;
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

    private final PhoneNumberUtil phoneNumberUtil;



    @Autowired
    public ValidatorService(Validator jakartaValidator,
                            PhoneNumberUtil phoneNumberUtil) {
        this.jakartaValidator = jakartaValidator;
        this.phoneNumberUtil = phoneNumberUtil;
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

    /**
     * Validates input using google's libphonenumber.
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
     * Validates using apache commons' email validator
     */
    public void validateEmail(String email) {
        if (!getEmailValidator().isValid(email))
            throw new InvalidEmailException(format("Email [%s] is not valid, provide a new one", email));
    }

    /**
     * Wrapping for testability
     */
    public EmailValidator getEmailValidator() {
        return EmailValidator.getInstance();
    }

    public void validateUsername(String username) {
        if (username == null || username.isBlank())
            throw new InvalidUsernameException(format("Username [%s] is not valid, provide a new one", username));
    }
}
