package com.xdavide9.sso.user.api;

import com.xdavide9.sso.jwt.JwtService;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.util.UserModifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * This service holds business logic for {@link UserController}.
 * It interacts with {@link UserRepository} for database operations. Methods
 * that modify the principal return a new jwt token to be used for authenticating if the request is successful,
 * an error otherwise.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class UserService {
    private final UserModifierService userModifierService;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserModifierService userModifierService,
                       JwtService jwtService) {
        this.userModifierService = userModifierService;
        this.jwtService = jwtService;
    }

    /**
     * Wrapping for usability and testability
     */
    protected User getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    @PreAuthorize("hasAnyAuthority('USER_GET', 'OPERATOR_GET', 'ADMIN_GET')")
    public User getPersonalDetails() {
        return getPrincipal();
    }

    @PreAuthorize("hasAnyAuthority('USER_PUT', 'OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> changeUsername(String username) {
        User principal = getPrincipal();
        userModifierService.setUsername(principal, username);
        String token = jwtService.generateToken(principal);
        return ResponseEntity.ok(token);
    }

    @PreAuthorize("hasAnyAuthority('USER_PUT', 'OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> changeEmail(String email) {
        User principal = getPrincipal();
        userModifierService.setEmail(principal, email);
        String token = jwtService.generateToken(principal);
        return ResponseEntity.ok(token);
    }

    @PreAuthorize("hasAnyAuthority('USER_PUT', 'OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> changePassword(String password) {
        User principal = getPrincipal();
        userModifierService.setPassword(principal, password);
        String token = jwtService.generateToken(principal);
        return ResponseEntity.ok(token);
    }

    @PreAuthorize("hasAnyAuthority('USER_PUT', 'OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> changePhoneNumber(String phoneNumber) {
        User principal = getPrincipal();
        userModifierService.setPhoneNumber(principal, phoneNumber);
        String token = jwtService.generateToken(principal);
        return ResponseEntity.ok(token);
    }

    @PreAuthorize("hasAnyAuthority('USER_PUT', 'OPERATOR_PUT', 'ADMIN_PUT')")
    public ResponseEntity<String> changeCountry(String countryCode) {
        User principal = getPrincipal();
        userModifierService.setCountry(principal, countryCode);
        String token = jwtService.generateToken(principal);
        return ResponseEntity.ok(token);
    }
}
