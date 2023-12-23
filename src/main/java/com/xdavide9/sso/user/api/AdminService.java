package com.xdavide9.sso.user.api;

import com.xdavide9.sso.exception.user.api.UserCannotBeModifiedException;
import com.xdavide9.sso.exception.user.api.UserExceptionReason;
import com.xdavide9.sso.exception.user.api.UserNotFoundException;
import com.xdavide9.sso.user.Role;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.lang.String.format;

/**
 * This service holds business logic for {@link AdminController}.
 * It interacts with {@link UserRepository} for database operations.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class AdminService {
    private final UserRepository userRepository;
    @Autowired
    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Finds user by UUID from repository.
     * Checks if the user is eligible for promotion and if
     * successful clears their authorities and changes their role.
     * @param uuid uuid of the user to be promoted
     * @throws UserNotFoundException with PROMOTION {@link UserExceptionReason} when the user is not found
     * @throws UserCannotBeModifiedException with PROMOTION {@link UserExceptionReason} when the user does not have USER {@link Role}
     * @return responseEntity with message for client
     */
    @PreAuthorize("hasAuthority('ADMIN_PUT')")
    public ResponseEntity<String> promoteUserToOperator(UUID uuid) {
        User user = userRepository.findById(uuid).orElseThrow(
                () -> new UserNotFoundException(
                        format("Could not find user with uuid [%s] to be promoted to Operator", uuid),
                        UserExceptionReason.PROMOTION
                ));
        if (!user.getRole().equals(Role.USER))
            throw new UserCannotBeModifiedException(
                    format("Could not promote user [%s] because they do not have USER role", uuid),
                    UserExceptionReason.PROMOTION
                    );
        user.getAuthorities().clear();
        user.setRole(Role.OPERATOR);
        userRepository.save(user);
        return ResponseEntity.ok(format("The user [%s] has been successfully promoted to operator", uuid));
    }

    /**
     * Finds user by UUID from repository.
     * Checks if the user is eligible for deletion and if
     * successful deletes them from the database.
     * @throws UserNotFoundException with DELETION {@link UserExceptionReason} when the user is not found
     * @throws UserCannotBeModifiedException with DELETION {@link UserExceptionReason} when the user to be deleted is an admin
     * @param uuid uuid of the user to be deleted
     * @return responseEntity with message for client
     */
    @PreAuthorize("hasAuthority('ADMIN_DELETE')")
    public ResponseEntity<String> deleteUser(UUID uuid) {
        User user = userRepository.findById(uuid).orElseThrow(
                () -> new UserNotFoundException(
                        format("Could not find user with uuid [%s] to be deleted", uuid),
                        UserExceptionReason.DELETION
                ));
        if (user.getRole().equals(Role.ADMIN))
            throw new UserCannotBeModifiedException(
              format("Could not delete user [%s] because they are an admin", uuid),
                    UserExceptionReason.DELETION
            );
        userRepository.delete(user);
        return ResponseEntity.ok(format("The user [%s] has been permanently deleted from the system", uuid));
    }

    /**
     * Finds user by UUID from repository.
     * Checks if the user is eligible for demotion and if successful
     * demotes them from operator role to user.
     * @throws UserNotFoundException with DEMOTION {@link UserExceptionReason} when the user is not found
     * @throws UserCannotBeModifiedException with DEMOTION {@link UserExceptionReason} when the user to be demoted is not an operator
     * @param uuid uuid of the user to be demoted
     * @return responseEntity with message for client
     */
    @PreAuthorize("hasAuthority('ADMIN_PUT')")
    public ResponseEntity<String> demoteUser(UUID uuid) {
        User user = userRepository.findById(uuid).orElseThrow(
                () -> new UserNotFoundException(
                        format("Could not find user with uuid [%s] to be demoted to a plain user", uuid),
                        UserExceptionReason.DEMOTION
                ));
        if (!user.getRole().equals(Role.OPERATOR))
            throw new UserCannotBeModifiedException(
              format("Could not demote user [%s] because they are not an operator", uuid),
              UserExceptionReason.DEMOTION
            );
        user.getAuthorities().clear();
        user.setRole(Role.USER);
        userRepository.save(user);
        return ResponseEntity.ok(format("The user [%s] has been demoted to a plain user", uuid));
    }
}
