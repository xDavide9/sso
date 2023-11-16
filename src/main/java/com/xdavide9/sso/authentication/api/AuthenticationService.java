package com.xdavide9.sso.authentication.api;

import com.xdavide9.sso.exception.authentication.api.EmailTakenException;
import com.xdavide9.sso.exception.authentication.api.PasswordTooShortException;
import com.xdavide9.sso.exception.authentication.api.UsernameTakenException;
import com.xdavide9.sso.jwt.JwtService;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.authentication.SignupRequest;
import com.xdavide9.sso.authentication.AuthenticationResponse;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

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
     * password encoder
     * @since 0.0.1-SNAPSHOT
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * default validator from jakarta api
     * @since 0.0.1-SNAPSHOT
     */
    private final Validator validator;

    private final UserDetailsService userDetailsService;

    /**
     * constructor
     * @param jwtService jwtService
     * @param repository repository
     * @param passwordEncoder encoder
     * @param validator default validator
     */
    @Autowired
    public AuthenticationService(JwtService jwtService,
                                 UserRepository repository,
                                 PasswordEncoder passwordEncoder,
                                 Validator validator,
                                 UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.userDetailsService = userDetailsService;
    }

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
        // checks
        String username = request.username();
        if (repository.existsByUsername(username)) {
            throw new UsernameTakenException(
                    format("Username [%s] is already taken", username)
            );
        }
        String email = request.email();
        if (repository.existsByEmail(email)) {
            throw new EmailTakenException(
                    format("Email [%s] is already taken", email)
            );
        }
        String password = request.password();
        // TODO delegate this feature to a PasswordValidator
        if (password.length() < 8)
            throw new PasswordTooShortException("Password must be at least 8 characters long");
        // process of registration
        User user = new User(username, email, passwordEncoder.encode(password));
        validator.validate(user);   // if not valid throws ConstraintViolationException
        repository.save(user);
        String token = jwtService.generateToken(user);
        AuthenticationResponse response = new AuthenticationResponse(token);
        return ResponseEntity.ok(response);
    }

    /**
     * This method holds business logic for the /login endpoint. It will
     * try to issue a token if the provided details in {@link LoginRequest} match against
     * the database queried via {@link UserRepository}. Either a username or email can be provided to login
     * and the existence of both is assured by various constraints at different levels in the application.
     * If login is successful a token is issued as a response via {@link AuthenticationResponse}
     * @param request login request
     * @return ResponseEntity of AuthenticationResponse
     * @since 0.0.1-SNAPSHOT
     */
    public ResponseEntity<AuthenticationResponse> login(LoginRequest request) {
        // checks
        String subject = request.subject();
        User user = (User) userDetailsService.loadUserByUsername(subject);
        // issue token
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}
