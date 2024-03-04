package com.xdavide9.sso.user.api;

import com.xdavide9.sso.user.fields.role.Role;
import com.xdavide9.sso.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * This controller exposes endpoints to let admins manage {@link User}s and Operators.
 * Requires the ADMIN {@link Role} within the system.
 * Delegates business logic to {@link AdminService}.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v0.0.1/users")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Add operator privileges to user with specified uuid.
     * @param uuid uuid of the user to be promoted
     */
    @PutMapping("/promote/{uuid}")
    @PreAuthorize("hasAuthority('ADMIN_PUT')")
    public ResponseEntity<String> promoteUserToOperator(@PathVariable UUID uuid) {
        return adminService.promoteUserToOperator(uuid);
    }

    /**
     * Bans user with specified uuid which means they cannot access the system until they are unbanned.
     * @param uuid uuid of the user to be banned
     */
    @DeleteMapping("/ban/{uuid}")
    @PreAuthorize("hasAuthority('ADMIN_DELETE')")
    public ResponseEntity<String> banUser(@PathVariable UUID uuid) {
        return adminService.banUser(uuid);
    }

    /**
     * Unbans user with specified uuid.
     * @param uuid uuid of the user to be unbanned
     */
    @PutMapping("/unban/{uuid}")
    @PreAuthorize("hasAuthority('ADMIN_PUT')")
    public ResponseEntity<String> unbanUser(@PathVariable UUID uuid) {
        return adminService.unbanUser(uuid);
    }

    /**
     * Demotes user with specified uuid from operator role to a plain User.
     * @param uuid uuid of the user to be demoted
     */
    @PutMapping("/demote/{uuid}")
    @PreAuthorize("hasAuthority('ADMIN_PUT')")
    public ResponseEntity<String> demoteUser(@PathVariable UUID uuid) {
        return adminService.demoteUser(uuid);
    }
}
