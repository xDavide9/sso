package com.xdavide9.sso.util;

import com.xdavide9.sso.user.PasswordDTO;
import com.xdavide9.sso.user.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

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

    private final Validator validator;

    @Autowired
    public ValidatorService(Validator validator) {
        this.validator = validator;
    }

    /**
     * Validates generic t object with jakarta constraints defined in T's class.
     * @param t object to be validated
     * @param <T> class that contains constraints
     */
    public <T> void validateUsingJakartaConstraints(T t) {
        Set<ConstraintViolation<T>> violations = validator.validate(t);
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
     * Validates input using google's libphonenumber
     */
    public void validatePhoneNumber(String phoneNumber) {

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
