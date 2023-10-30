package com.xdavide9.sso.user.api;

import com.xdavide9.sso.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service holds business logic for {@link AdminController}.
 * It interacts with {@link UserRepository} for database operations.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class AdminService {

    /**
     * userRepository
     * @see UserRepository
     * @since v0.0.1-SNAPSHOT
     */
    private final UserRepository userRepository;

    /**
     * constructor
     * @param userRepository userRepository
     * @since v0.0.1-SNAPSHOT
     */
    @Autowired
    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
