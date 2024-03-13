package com.xdavide9.sso.user.change;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Delegates business logic to {@link UserChangeService}.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
@RestController
@RequestMapping("/api/v0.0.1/users/changes")
public class UserChangeController {

    private final UserChangeService userChangeService;

    @Autowired
    public UserChangeController(UserChangeService userChangeService) {
        this.userChangeService = userChangeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public List<UserChange> getAllChanges() {
        return userChangeService.getAllChanges();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public UserChange getChange(@PathVariable("id") Long id) {
        return userChangeService.getChange(id);
    }

    @GetMapping("/user/{uuid}")
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public List<UserChange> getChangesPerUser(@PathVariable("uuid") UUID uuid) {
        return userChangeService.getChangesPerUser(uuid);
    }
}
