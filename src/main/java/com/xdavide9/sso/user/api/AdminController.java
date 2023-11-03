package com.xdavide9.sso.user.api;

import com.xdavide9.sso.user.Role;
import com.xdavide9.sso.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO implement security configuration for these endpoints
// TODO implement admin only functionality including destructive operation like delete

/**
 * This controller exposes endpoints to let admins manage {@link User}s and Operators.
 * Requires the ADMIN {@link Role} within the system.
 * Delegates business logic to {@link AdminService}.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v0.0.1-SNAPSHOT/admin/users") // api/version/role/users
public class AdminController {

    /**
     * adminService
     * @see AdminService
     * @since 0.0.1-SNAPSHOT
     */
    private final AdminService adminService;

    /**
     * constructor
     * @param adminService adminService
     * @since 0.0.1-SNAPSHOT
     */
    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
}
