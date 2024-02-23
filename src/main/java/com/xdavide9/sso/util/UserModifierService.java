package com.xdavide9.sso.util;

import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public <T> void setAttribute(User user, T value, BiConsumer<User, T> setter) {
        setter.accept(user, value);
        validatorService.validateUser(user);
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
}
