package com.xdavide9.sso.user.api;

import com.xdavide9.sso.exception.user.api.UserNotFoundException;
import com.xdavide9.sso.exception.user.api.UserExceptionReason;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserDTO;
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
     * @return all users in database
     * @see OperatorController#getUsers()
     */
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public List<UserDTO> getUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(UserDTO::fromUser)
                .toList();
    }

    /**
     * Gets user with specified uuid from the database.
     * In case of failure it throws a {@link UserNotFoundException} with INFORMATION
     * {@link UserExceptionReason}.
     * @see OperatorController#getUserByUuid(UUID)
     * @param uuid of the user in question
     * @return the user in question
     */
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public UserDTO getUserByUuid(UUID uuid) {
        User user = userRepository
                .findById(uuid)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                String.format("User with uuid [%s] not found.", uuid),
                                UserExceptionReason.INFORMATION
                        ));
        return UserDTO.fromUser(user);
    }

    /**
     * Gets user with specified username from the database.
     * In case of failure it throws a {@link UserNotFoundException} with INFORMATION
     * {@link UserExceptionReason}.
     * @see OperatorController#getUserByUsername(String)
     * @param username of the user in question
     * @return the user in question
     */
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public UserDTO getUserByUsername(String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                String.format("User with username [%s] not found.", username),
                                UserExceptionReason.INFORMATION
                        ));
        return UserDTO.fromUser(user);
    }

    /**
     * Gets user with specified email from the database.
     * In case of failure it throws a {@link UserNotFoundException} with INFORMATION
     * {@link UserExceptionReason}.
     * @see OperatorController#getUserByEmail(String)
     * @param email of the user in question
     * @return the user in question
     */
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public UserDTO getUserByEmail(String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                String.format("User with email [%s] not found.", email),
                                UserExceptionReason.INFORMATION
                        ));
        return UserDTO.fromUser(user);
    }
}
