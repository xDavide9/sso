package com.xdavide9.sso.authentication.api;

import com.xdavide9.sso.authentication.AuthenticationResponse;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.authentication.RepositoryUserDetailsService;
import com.xdavide9.sso.authentication.SignupRequest;
import com.xdavide9.sso.config.SecurityConfig;
import com.xdavide9.sso.exception.authentication.api.EmailTakenException;
import com.xdavide9.sso.exception.authentication.api.IncorrectPasswordException;
import com.xdavide9.sso.exception.authentication.api.UsernameTakenException;
import com.xdavide9.sso.exception.user.api.UserBannedException;
import com.xdavide9.sso.jwt.JwtService;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.util.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * This service holds business logic for {@link AuthenticationController}.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 * @see AuthenticationController
 */
@Service
public class AuthenticationService {
    /**
     * Service that allows to work with jwt with ease.
     */
    private final JwtService jwtService;
    /**
     * Jpa repository to interact with the database.
     */
    private final UserRepository repository;

    /**
     * It is the default bcrypt implementation defined in {@link SecurityConfig}.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Service that validates user input to make sure it adheres to specific constraints.
     */
    private final ValidatorService validatorService;

    /**
     * It is the {@link RepositoryUserDetailsService} implementation of {@link UserDetailsService}.
     */
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthenticationService(JwtService jwtService,
                                 UserRepository repository,
                                 PasswordEncoder passwordEncoder,
                                 ValidatorService validatorService,
                                 @Qualifier(value = "repositoryUserDetailsService") UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.validatorService = validatorService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * This method holds business logic for the /signup endpoint. It will
     * try to register a new account in the database using {@link UserRepository} if the provided details
     * in {@link SignupRequest} adhere to specific constraints. These constraints are: uniqueness of username and email
     * provided and a strong enough password. If all the above is met a new account is created in the system and
     * a token is issued as a response via {@link AuthenticationResponse}. Failure is handled by appropriate exceptions.
     * @param request signup request containing username, email and password
     * @return ResponseEntity of AuthenticationResponse
     */
    public ResponseEntity<AuthenticationResponse> signup(SignupRequest request) {
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
        // validate fields
        validatorService.validateUsername(username);
        validatorService.validateEmail(email);
        validatorService.validateRawPassword(request.password());
        User user = new User(username, email, passwordEncoder.encode(request.password()));
        validatorService.validateUser(user);    // if not valid throws ConstraintViolationException
        repository.save(user);
        String token = jwtService.generateToken(user);
        AuthenticationResponse response = new AuthenticationResponse(token);
        return ResponseEntity.ok(response);
    }

    /**
     * This method holds business logic for the /login endpoint. It will
     * try to issue a token if the provided details in {@link LoginRequest} match against
     * the database queried via {@link UserRepository}. Either a username or email can be provided to log in
     * and the existence of both is assured by various constraints at different levels in the application.
     * If login is successful a token is issued as a response via {@link AuthenticationResponse}.
     * If the password is incorrect an appropriate {@link IncorrectPasswordException} is thrown.
     * @param request login request containing the subject and the password
     * @return ResponseEntity of AuthenticationResponse
     */
    public ResponseEntity<AuthenticationResponse> login(LoginRequest request) {
        // checks
        String subject = request.subject();
        String rawPassword = request.password();
        User user = (User) userDetailsService.loadUserByUsername(subject);
        String encodedPassword = user.getPassword();
        if (!passwordEncoder.matches(rawPassword, encodedPassword))
            throw new IncorrectPasswordException(format("Incorrect input password at login for subject [%s]", subject));
        if (!user.isEnabled())
            throw new UserBannedException(format("The account with subject [%s] is banned.", subject));
        // issue token
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}
