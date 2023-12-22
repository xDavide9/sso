package com.xdavide9.sso.user.api;

import com.xdavide9.sso.exception.user.api.UserNotFoundException;
import com.xdavide9.sso.exception.user.api.UserExceptionReason;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * Gets all users from the database.
     * @since 0.0.1-SNAPSHOT
     * @return all users in database
     * @see OperatorController#getUsers()
     */
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Gets user with specified uuid from the database.
     * In case of failure it throws a {@link UserNotFoundException} with an appropriate
     * {@link UserExceptionReason}.
     * @since 0.0.1-SNAPSHOT
     * @see OperatorController#getUserByUuid(UUID)
     * @param uuid of the user in question
     * @return the user in question
     */
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public User getUserByUuid(UUID uuid) {
        return userRepository
                .findById(uuid)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                String.format("User with uuid [%s] not found.", uuid),
                                UserExceptionReason.INFORMATION
                        ));
    }

    /**
     * Gets user with specified username from the database.
     * In case of failure it throws a {@link UserNotFoundException} with an appropriate
     * {@link UserExceptionReason}.
     * @since 0.0.1-SNAPSHOT
     * @see OperatorController#getUserByUsername(String)
     * @param username of the user in question
     * @return the user in question
     */
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public User getUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                String.format("User with username [%s] not found.", username),
                                UserExceptionReason.INFORMATION
                        ));
    }

    /**
     * Gets user with specified email from the database.
     * In case of failure it throws a {@link UserNotFoundException} with an appropriate
     * {@link UserExceptionReason}.
     * @since 0.0.1-SNAPSHOT
     * @see OperatorController#getUserByEmail(String)
     * @param email of the user in question
     * @return the user in question
     */
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public User getUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                String.format("User with email [%s] not found.", email),
                                UserExceptionReason.INFORMATION
                        ));
    }
}
