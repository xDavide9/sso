package com.xdavide9.sso.user.api;

import com.xdavide9.sso.user.fields.role.Role;
import com.xdavide9.sso.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO implement security configuration for these endpoints
// TODO validate user paylods sent in post methods with hibernate validation that has already been tested
// TODO implement GET methods that use the jwt token subject to get information about the currently logged in user to retrieve personal information

/**
 * This controller exposes endpoints for {@link User}s to manage their personal information.
 * Requires the USER {@link Role} within the system.
 * Delegates business logic to {@link UserService}.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v0.0.1/user/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
}
