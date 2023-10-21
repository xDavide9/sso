package com.xdavide9.sso.user.api;

import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

// TODO implement security configuration for these endpoints

/**
 * This controller exposes endpoints to let operators manage {@link User}s.
 * Requires the OPERATOR {@link Role} within the system.
 * Delegates business logic to {@link OperatorService}.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v0.0.1-SNAPSHOT/users")
public class OperatorController {
    private final OperatorService userService;

    @Autowired
    public OperatorController(OperatorService userService) {
        this.userService = userService;
    }

    /**
     * @return all users saved in database
     * @see OperatorService#getUsers()
     * @author xdavide9
     * @since 0.0.1-SNAPSHOT
     */
    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    /**
     * @see OperatorService#getUserByUuid(UUID)
     * @author xdavide9
     * @since 0.0.1-SNAPSHOT
     * @param uuid of the user in question
     * @return the user in question
     */
    @GetMapping("/uuid/{uuid}")
    public User getUserByUuid(@PathVariable UUID uuid) {
        return userService.getUserByUuid(uuid);
    }

    /**
     * @see OperatorService#getUserByUsername(String)
     * @author xdavide9
     * @since 0.0.1-SNAPSHOT
     * @param username of the user in question
     * @return the user in question
     */
    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    /**
     * @see OperatorService#getUserByEmail(String)
     * @author xdavide9
     * @since 0.0.1-SNAPSHOT
     * @param email of the user in question
     * @return the user in question
     */
    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

}
