package com.xdavide9.sso.user.api;

import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * This service holds business logic for {@link UserController}.
 * It interacts with {@link UserRepository} for database operations.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * @author xdavide9
     * @since 0.0.1-SNAPSHOT
     * @return all users in database
     * @see UserController#getUsers()
     */
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * @author xdavide9
     * @since 0.0.1-SNAPSHOT
     * @see UserController#getUserByUuid(UUID)
     * @param uuid of the user in question
     * @return the user in question
     */
    public User getUserByUuid(UUID uuid) {
        // TODO implement custom exceptions and exception handling
        return userRepository
                .findById(uuid)
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format("User with uuid [%s] not found.", uuid))
                );
    }
}
