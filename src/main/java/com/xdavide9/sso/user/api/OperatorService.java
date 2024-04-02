package com.xdavide9.sso.user.api;

import com.xdavide9.sso.exception.user.api.UserCannotBeModifiedException;
import com.xdavide9.sso.exception.user.api.UserExceptionReason;
import com.xdavide9.sso.exception.user.api.UserNotFoundException;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.user.fields.role.Role;
import com.xdavide9.sso.util.TimeOutService;
import com.xdavide9.sso.util.UserModifierService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

/**
 * This service holds business logic for {@link OperatorController}.
 * It interacts with {@link UserRepository} for database operations.
 * These methods can be used by an operator or people with higher role and include
 * getting information about users, or a specific user with different input parameters,
 * timing users out and changing their username or email (used to authenticate)
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

    /**
     * Times out {@link User} with specified {@link UUID} for specified duration.
     * If duration is not provided a default value is used.
     * If the user is already timed out or banned the request is blocked.
     * @param uuid - of the user to be timed out
     * @param duration - to time out the user for
     * @return {@link ResponseEntity}
     */
    @Transactional
    @PreAuthorize("hasAnyAuthority('OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> timeOut(UUID uuid, Long duration) {
        User principal = (User) securityContext().getAuthentication().getPrincipal();
        User user = userRepository
                .findById(uuid)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                format("User with uuid [%s] not found.", uuid),
                                UserExceptionReason.TIMEOUT
                        ));
        if (principal.getRole().equals(Role.OPERATOR) && !user.getRole().equals(Role.USER))
            throw new AccessDeniedException(format("Access Denied. You cannot time out" +
                    " user with uuid [%s] because they are an operator or admin.", uuid));
        if (!user.isEnabled())
            throw new UserCannotBeModifiedException(format("User with uuid [%s] is already banned or timed out", uuid), UserExceptionReason.TIMEOUT);
        if (duration == null) {
            timeOutService.timeOut(user);
            return ResponseEntity.ok(format("User with uuid [%s] has been timed out for 30 minutes.", uuid));
        }
        timeOutService.timeOut(user, duration);
        return ResponseEntity.ok(format("User with uuid [%s] has been timed out for [%d] minutes.", uuid, duration));
    }

    /**
     * Changes the username of {@link User} with specified {@link UUID}.
     * An admin can change every user's username.
     * An operator can change the username of users with {@link Role} USER.
     * @param uuid - of the user change the username of
     * @param username - to be changed
     * @return {@link ResponseEntity}
     */
    @Transactional
    @PreAuthorize("hasAnyAuthority('OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> changeUsername(UUID uuid, String username) {
        User principal = (User) securityContext().getAuthentication().getPrincipal();
        User user = getUserByUuid(uuid);
        if (principal.getRole().equals(Role.OPERATOR) && !user.getRole().equals(Role.USER))
            throw new AccessDeniedException(format("Access Denied. You cannot change the username of" +
                    " user with uuid [%s] because they are an operator or admin.", uuid));
        userModifierService.setUsername(user, username);
        return ResponseEntity.ok(format("Username of user with uuid [%s] has been changed correctly to [%s]", uuid, username));
    }

    /**
     * Changes the email of {@link User} with specified {@link UUID}.
     * An admin can change every user's email.
     * An operator can change the username of users with {@link Role} USER.
     * @param uuid - of the user change the email of
     * @param email - to be changed
     * @return {@link ResponseEntity}
     */
    @Transactional
    @PreAuthorize("hasAnyAuthority('OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> changeEmail(UUID uuid, String email) {
        User principal = (User) securityContext().getAuthentication().getPrincipal();
        User user = getUserByUuid(uuid);
        if (principal.getRole().equals(Role.OPERATOR) && !user.getRole().equals(Role.USER))
            throw new AccessDeniedException(format("Access Denied. You cannot change the email of" +
                    " user with uuid [%s] because they are an operator or admin.", uuid));
        userModifierService.setEmail(user, email);
        return ResponseEntity.ok(format("Email of user with uuid [%s] has been changed correctly to [%s]", uuid, email));
    }

    /**
     * Wrapping security context holder for testability (a static utility cannot be mocked).
     * @return {@link SecurityContext} object that is not static
     */
    protected SecurityContext securityContext() {
        return SecurityContextHolder.getContext();
    }
}
