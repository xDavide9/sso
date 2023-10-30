package com.xdavide9.sso.user.api;

import com.xdavide9.sso.user.Role;
import com.xdavide9.sso.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO implement security configuration for these endpoints

/**
 * This controller exposes endpoints for {@link User}s to manage their personal information.
 * Requires the USER {@link Role} within the system.
 * Delegates business logic to {@link UserService}.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v0.0.1-SNAPSHOT/user/users")  // api/version/role/users
public class UserController {

    /**
     * userService
     * @see UserService
     * @since 0.0.1-SNAPSHOT
     */
    private final UserService userService;

    /**
     * constructor
     * @param userService userService
     * @since 0.0.1-SNAPSHOT
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
}
