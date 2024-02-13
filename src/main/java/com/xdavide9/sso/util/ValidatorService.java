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
 * This service is used to validate fields of {@link User} instances meaning that each of these fields
 * must adhere to the constraints defined in the {@link User} class. If this fails to happen
 * a {@link ConstraintViolationException} with all the violations is thrown.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
@Service
public class ValidatorService {
    private final Validator validator;

    @Autowired
    public ValidatorService(Validator validator) {
        this.validator = validator;
    }

    /**
     * Validates input user by applying constraints defined in {@link User} class.
     */
    public void validate(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty())
            throw new ConstraintViolationException(violations);
    }

    /**
     * Validates input password by applying constraints defined in {@link User} class.
     */
    public void validate(PasswordDTO passwordDTO) {
        Set<ConstraintViolation<PasswordDTO>> violations = validator.validate(passwordDTO);
        if (!violations.isEmpty())
            throw new ConstraintViolationException(violations);
    }
}
