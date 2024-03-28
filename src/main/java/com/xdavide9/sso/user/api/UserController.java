package com.xdavide9.sso.user.api;

import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.fields.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * This controller exposes endpoints for {@link User}s to manage their personal information.
 * Requires the USER {@link Role} within the system.
 * Delegates business logic to {@link UserService}.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v0.0.1/principal")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER_GET', 'OPERATOR_GET', 'ADMIN_GET')")
    public User getPersonalDetails() {
        return userService.getPersonalDetails();
    }

    @PutMapping("/change/username")
    @PreAuthorize("hasAnyAuthority('USER_PUT', 'OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> changeUsername(@RequestParam(name = "value") String username) {
        return userService.changeUsername(username);
    }

    @PutMapping("/change/email")
    @PreAuthorize("hasAnyAuthority('USER_PUT', 'OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> changeEmail(@RequestParam(name = "value") String email) {
        return userService.changeEmail(email);
    }

    @PutMapping("/change/password")
    @PreAuthorize("hasAnyAuthority('USER_PUT', 'OPERATOR_PUT', 'ADMIN_PUT')")
    public void changePassword(@RequestParam(name = "value") String password) {
        userService.changePassword(password);
    }

    @PutMapping("/change/phoneNumber")
    @PreAuthorize("hasAnyAuthority('USER_PUT', 'OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> changePhoneNumber(@RequestParam(name = "value") String phoneNumber) {
        return userService.changePhoneNumber(phoneNumber);
    }

    /**
     * Accepts the ISO 3166-1 alpha-2 code
     */
    @PutMapping("/change/country")
    @PreAuthorize("hasAnyAuthority('USER_PUT', 'OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> changeCountry(@RequestParam(name = "value") String countryCode) {
        return userService.changeCountry(countryCode);
    }
}
