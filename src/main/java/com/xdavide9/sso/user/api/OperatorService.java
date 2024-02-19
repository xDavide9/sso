package com.xdavide9.sso.user.api;

import com.xdavide9.sso.exception.authentication.api.EmailTakenException;
import com.xdavide9.sso.exception.authentication.api.UsernameTakenException;
import com.xdavide9.sso.exception.user.api.UserCannotBeModifiedException;
import com.xdavide9.sso.exception.user.api.UserNotFoundException;
import com.xdavide9.sso.exception.user.api.UserExceptionReason;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.util.TimeOutService;
import com.xdavide9.sso.util.UserModifierService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

/**
 * This service holds business logic for {@link OperatorController}.
 * It interacts with {@link UserRepository} for database operations.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class OperatorService {
    private final UserRepository userRepository;
    private final TimeOutService timeOutService;
    private final UserModifierService userModifierService;

    @Autowired
    public OperatorService(UserRepository userRepository,
                           TimeOutService timeOutService,
                           UserModifierService userModifierService) {
        this.userRepository = userRepository;
        this.timeOutService = timeOutService;
        this.userModifierService = userModifierService;
    }


    /**
     * Gets all users from the database.
     * @return all users in database
     * @see OperatorController#getUsers()
     */
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public List<User> getUsers() {
        return userRepository
                .findAll()
                .stream()
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
    public User getUserByUuid(UUID uuid) {
        return userRepository
                .findById(uuid)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                format("User with uuid [%s] not found.", uuid),
                                UserExceptionReason.INFORMATION
                        ));
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
    public User getUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                format("User with username [%s] not found.", username),
                                UserExceptionReason.INFORMATION
                        ));
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
    public User getUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                format("User with email [%s] not found.", email),
                                UserExceptionReason.INFORMATION
                        ));
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> timeOut(UUID uuid, Long duration) {
        User user = userRepository
                .findById(uuid)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                format("User with uuid [%s] not found.", uuid),
                                UserExceptionReason.TIMEOUT
                        ));
        if (!user.isEnabled())
            throw new UserCannotBeModifiedException(format("User with uuid [%s] is already banned or timed out", uuid), UserExceptionReason.TIMEOUT);
        if (duration == null) {
            timeOutService.timeOut(user);
            return ResponseEntity.ok(format("User with uuid [%s] has been timed out for the default duration", uuid));
        }
        timeOutService.timeOut(user, duration);
        return ResponseEntity.ok(format("User with uuid [%s] has been timed out for [%d] milliseconds", uuid, duration));
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> changeUsername(UUID uuid, String username) {
        User user = getUserByUuid(uuid);
        if (userRepository.existsByUsername(username))
            throw new UsernameTakenException(format("Cannot change username of user with uuid [%s]", uuid));
        userModifierService.setUsername(user, username);
        return ResponseEntity.ok(format("Username of user with uuid [%s] has been changed correctly to [%s]", uuid, username));
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> changeEmail(UUID uuid, String email) {
        User user = getUserByUuid(uuid);
        if (userRepository.existsByEmail(email))
            throw new EmailTakenException(format("Cannot change email of user with uuid [%s]", uuid));
        userModifierService.setEmail(user, email);
        return ResponseEntity.ok(format("Email of user with uuid [%s] has been changed correctly to [%s]", uuid, email));
    }
}
