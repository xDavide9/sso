package com.xdavide9.sso.user.api;

import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * This controller exposes endpoints to let operators manage {@link User}s.
 * Requires the OPERATOR {@link Role} within the system.
 * Delegates business logic to {@link OperatorService}.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v0.0.1/users")
public class OperatorController {
    private final OperatorService operatorService;

    @Autowired
    public OperatorController(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    /**
     * Gets all users from the database.
     * @return all users saved in database
     * @see OperatorService#getUsers()
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public List<User> getUsers() {
        return operatorService.getUsers();
    }

    /**
     * Gets user with specified UUID.
     * @see OperatorService#getUserByUuid(UUID)
     * @param uuid of the user in question
     * @return the user in question
     */
    @GetMapping("/uuid/{uuid}")
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public User getUserByUuid(@PathVariable UUID uuid) {
        return operatorService.getUserByUuid(uuid);
    }

    /**
     * Gets user with specified Username.
     * @see OperatorService#getUserByUsername(String)
     * @param username of the user in question
     * @return the user in question
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public User getUserByUsername(@PathVariable String username) {
        return operatorService.getUserByUsername(username);
    }

    /**
     * Gets user with specified Email.
     * @see OperatorService#getUserByEmail(String)
     * @param email of the user in question
     * @return the user in question
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public User getUserByEmail(@PathVariable String email) {
        return operatorService.getUserByEmail(email);
    }

    // TODO add requests of type Put (e.g time out with the "enabled" field)

}
