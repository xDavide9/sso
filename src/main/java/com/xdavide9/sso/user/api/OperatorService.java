package com.xdavide9.sso.user.api;

import com.xdavide9.sso.exception.UserNotFoundException;
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
    /**
     * repository
     * @since 0.0.1-SNAPSHOT
     * @see UserRepository
     */
    private final UserRepository userRepository;

    /**
     * constructor
     * @param userRepository userRepository
     */
    @Autowired
    public OperatorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * service method
     * @since 0.0.1-SNAPSHOT
     * @return all users in database
     * @see OperatorController#getUsers()
     */
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * service method
     * @since 0.0.1-SNAPSHOT
     * @see OperatorController#getUserByUuid(UUID)
     * @param uuid of the user in question
     * @return the user in question
     */
    public User getUserByUuid(UUID uuid) {
        return userRepository
                .findById(uuid)
                .orElseThrow(() ->
                        new UserNotFoundException(String.format("User with uuid [%s] not found.", uuid))
                );
    }

    /**
     * service method
     * @since 0.0.1-SNAPSHOT
     * @see OperatorController#getUserByUsername(String)
     * @param username of the user in question
     * @return the user in question
     */
    public User getUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(String.format("User with username [%s] not found.", username))
                );
    }

    /**
     * service method
     * @since 0.0.1-SNAPSHOT
     * @see OperatorController#getUserByEmail(String)
     * @param email of the user in question
     * @return the user in question
     */
    public User getUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(String.format("User with email [%s] not found.", email))
                );
    }
}
