package com.xdavide9.sso.user.api;

import com.xdavide9.sso.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service holds business logic for {@link UserController}.
 * It interacts with {@link UserRepository} for database operations.
 * @author xdavide9
 * @since œ.0.1-SNAPSHOT
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    /**
     * constructor
     * @since 0.0.1-SNAPSHOT
     * @param userRepository repository
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
