package com.xdavide9.sso.user.api;

import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * This service holds business logic for {@link OperatorController}.
 * It interacts with {@link UserRepository} for database operations.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class OperatorService {
    private final UserRepository userRepository;

    @Autowired
    public OperatorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * @author xdavide9
     * @since 0.0.1-SNAPSHOT
     * @return all users in database
     * @see OperatorController#getUsers()
     */
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * @author xdavide9
     * @since 0.0.1-SNAPSHOT
     * @see OperatorController#getUserByUuid(UUID)
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

    /**
     * @author xdavide9
     * @since 0.0.1-SNAPSHOT
     * @see OperatorController#getUserByUsername(String)
     * @param username of the user in question
     * @return the user in question
     */
    public User getUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format("User with username [%s] not found.", username))
                );
    }

    /**
     * @author xdavide9
     * @since 0.0.1-SNAPSHOT
     * @see OperatorController#getUserByEmail(String)
     * @param email of the user in question
     * @return the user in question
     */
    public User getUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format("User with email [%s] not found.", email))
                );
    }
}
