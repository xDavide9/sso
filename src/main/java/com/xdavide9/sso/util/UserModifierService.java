package com.xdavide9.sso.util;

import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.user.fields.Gender;
import com.xdavide9.sso.user.fields.country.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.function.BiConsumer;

/**
 * This service contains methods that change {@link User}s attributes.
 * It does so by first validating them using {@link ValidatorService} in different ways
 * and then saving to db with {@link UserRepository}.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
@Service
public class UserModifierService {

    // TODO update this class to persist the changes made to the user
    // remember to ensure consistency across entities
    // (e.g. country when a user changes its country remove them from the list of user associated with that country)

    private final UserRepository repository;
    private final ValidatorService validatorService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserModifierService(UserRepository repository,
                               ValidatorService validatorService,
                               PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.validatorService = validatorService;
        this.passwordEncoder = passwordEncoder;
    }

    private <T> void setAttribute(User user, T value, BiConsumer<User, T> setter) {
        setter.accept(user, value);
        repository.save(user);
    }

    public void setUsername(User user, String username) {
        validatorService.validateUsername(username);
        setAttribute(user, username, User::setUsername);
    }

    public void setEmail(User user, String email) {
        validatorService.validateEmail(email);
        setAttribute(user, email, User::setEmail);
    }

    public void setPassword(User user, String password) {
        validatorService.validateRawPassword(password);
        setAttribute(user, passwordEncoder.encode(password), User::setPassword);
    }

    public void setPhoneNumber(User user, String phoneNumber) {
        validatorService.validatePhoneNumber(phoneNumber);
        setAttribute(user, phoneNumber, User::setPhoneNumber);
    }

    public void setCountry(User user, Country country) {
        validatorService.validateCountry(country);
        setAttribute(user, country, User::setCountry);
    }

    public void setDateOfBirth(User user, LocalDate dateOfBirth) {
        validatorService.validateDateOfBirth(dateOfBirth);
        setAttribute(user, dateOfBirth, User::setDateOfBirth);
    }

    public void setFirstName(User user, String firstName) {
        setAttribute(user, firstName, User::setFirstName);
    }

    public void setLastName(User user, String lastName) {
        setAttribute(user, lastName, User::setLastName);
    }

    public void setGender(User user, Gender gender) {
        setAttribute(user, gender, User::setGender);
    }
}
