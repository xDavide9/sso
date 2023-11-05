package com.xdavide9.sso.registration.api;

import com.xdavide9.sso.registration.SignupRequest;
import com.xdavide9.sso.registration.LoginRequest;
import com.xdavide9.sso.registration.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller holds the /signup and /login endpoints which are whitelisted
 * and allow users to receive jwt tokens to authenticate across the application.
 * The /signup endpoint is meant for users that need to register for the firs time to the application.
 * Therefore, they will need to provide a username, email and password which are the required fields
 * by the {@link com.xdavide9.sso.user.User}. Additional fields may be set later in appropriate account management
 * section exposed by {@link com.xdavide9.sso.user.api.UserController}.
 * It delegates business logic to {@link AuthenticationService}.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 * @see AuthenticationService
 */
@RestController
public class AuthenticationController {

    /**
     * authenticationService
     * @since 0.0.1-SNAPSHOT
     */
    private final AuthenticationService authenticationService;

    /**
     * constructor
     * @since 0.0.1-SNAPSHOT
     * @param authenticationService authenticationService
     */
    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Method that maps the /signup endpoint. It requires a {@link SignupRequest}
     * that holds the username, email and password
     * @param request signup request
     * @return ResponseEntity of AuthenticationResponse
     * @since 0.0.1-SNAPSHOT
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> signup(@RequestBody SignupRequest request) {
        return authenticationService.signup(request);
    }

    /**
     * Method that maps the /login endpoint. It requires a {@link LoginRequest}
     * that holds either the username or email, and the password of the account.
     * @param request Login request
     * @return ResponseEntity of AuthenticationResponse
     * @since 0.0.1-SNAPSHOT
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        return authenticationService.login(request);
    }
}
