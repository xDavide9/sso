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
 * This controller exposes endpoints to let operators manage {@link User}s
 * under the url /api/vX/operator/users where X is the current version of the application.
 * Requires the OPERATOR {@link Role} within the system.
 * Delegates business logic to {@link OperatorService}.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v${app.version}/operator/users")  // api/version/role/users
public class OperatorController {
    /**
     * operatorService
     * @since 0.0.1-SNAPSHOT
     * @see OperatorService
     */
    private final OperatorService operatorService;

    /**
     * constructor
     * @param operatorService operatorService
     * @since 0.0.1-SNAPSHOT
     */
    @Autowired
    public OperatorController(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    /**
     * Http get method
     * @return all users saved in database
     * @see OperatorService#getUsers()
     * @since 0.0.1-SNAPSHOT
     */
    @GetMapping
    @PreAuthorize("hasAuthority('OPERATOR_GET')")
    public List<User> getUsers() {
        return operatorService.getUsers();
    }

    /**
     * Http get method
     * @see OperatorService#getUserByUuid(UUID)
     * @since 0.0.1-SNAPSHOT
     * @param uuid of the user in question
     * @return the user in question
     */
    @GetMapping("/uuid/{uuid}")
    @PreAuthorize("hasAuthority('OPERATOR_GET')")
    public User getUserByUuid(@PathVariable UUID uuid) {
        return operatorService.getUserByUuid(uuid);
    }

    /**
     * Http get method
     * @see OperatorService#getUserByUsername(String)
     * @since 0.0.1-SNAPSHOT
     * @param username of the user in question
     * @return the user in question
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("hasAuthority('OPERATOR_GET')")
    public User getUserByUsername(@PathVariable String username) {
        return operatorService.getUserByUsername(username);
    }

    /**
     * Http get method
     * @see OperatorService#getUserByEmail(String)
     * @since 0.0.1-SNAPSHOT
     * @param email of the user in question
     * @return the user in question
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAuthority('OPERATOR_GET')")
    public User getUserByEmail(@PathVariable String email) {
        return operatorService.getUserByEmail(email);
    }

    // TODO add requests of type Put (e.g time out with the "enabled" field)

}
