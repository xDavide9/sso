package com.xdavide9.sso.authentication.api;

import com.xdavide9.sso.jwt.JwtService;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.authentication.SignupRequest;
import com.xdavide9.sso.authentication.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * This service holds business logic for {@link AuthenticationController}.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 * @see AuthenticationService
 */
@Service
public class AuthenticationService {

    /**
     * jwtService
     * @since 0.0.1-SNAPSHOT
     */
    private final JwtService jwtService;

    /**
     * user repository
     * @since 0.0.1-SNAPSHOT
     */
    private final UserRepository repository;

    /**
     * constructor
     * @param jwtService jwtService
     * @param repository repository
     */
    @Autowired
    public AuthenticationService(JwtService jwtService, UserRepository repository) {
        this.jwtService = jwtService;
        this.repository = repository;
    }

    // TODO implement business logic (don't forget to use hibernate validator with tested constrainst in User model), exceptions and update documentation

    /**
     * This method holds business logic for the /signup endpoint. It will
     * try to register a new account in the database using {@link UserRepository} if the provided details
     * in {@link SignupRequest} adhere to specific constraints. These constraints are: uniqueness of username and email
     * provided and a strong enough password. If all the above is met a new account is created in the system and
     * a token is issued as a response via {@link AuthenticationResponse}. Failure is handled by appropriate exceptions.
     * @param request singup request
     * @return ResponseEntity of AuthenticationResponse
     */
    public ResponseEntity<AuthenticationResponse> signup(SignupRequest request) {
        // validate user input with hibernate validator
        // register to database if not present otherwise throw exception
        // issue token
        return ResponseEntity.ok(AuthenticationResponse.builder().token("token123").build());
    }

    /**
     * This method holds business logic for the /login endpoint. It will
     * try to issue a token if the provided details in {@link LoginRequest} match against
     * the database queried via {@link UserRepository}. Either a username or email can be provided to login
     * and the existence of both is assured by various constraints at different levels in the application.
     * If login is successful a token is issued as a response via {@link AuthenticationResponse}
     * @param request login request
     * @return ResponseEntity of AuthenticationResponse
     */
    public ResponseEntity<AuthenticationResponse> login(LoginRequest request) {
        // check credentials against database otherwise throw exception
        // issue token
        return ResponseEntity.ok(AuthenticationResponse.builder().token("token123").build());
    }
}
