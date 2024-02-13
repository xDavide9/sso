package com.xdavide9.sso.util;

import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;

/**
 * This service contains methods that change {@link User}s attributes.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
@Service
public class UserModifierService {

    private final UserRepository repository;

    @Autowired
    public UserModifierService(UserRepository repository) {
        this.repository = repository;
    }

    public <T> void setAttribute(User user, T value, BiConsumer<User, T> setter) {
        setter.accept(user, value);
        repository.save(user);
    }

    public void setUsername(User user, String username) {
        setAttribute(user, username, User::setUsername);
    }

    public void setEmail(User user, String email) {
        setAttribute(user, email, User::setEmail);
    }

    public void setPassword(User user, String password) {
        setAttribute(user, password, User::setPassword);
    }

    public void setPhoneNumber(User user, String phoneNumber) {
        setAttribute(user, phoneNumber, User::setPhoneNumber);
    }
}
