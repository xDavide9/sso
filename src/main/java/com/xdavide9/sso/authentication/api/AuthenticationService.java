package com.xdavide9.sso.authentication.api;

import com.xdavide9.sso.authentication.AuthenticationResponse;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.authentication.RepositoryUserDetailsService;
import com.xdavide9.sso.authentication.SignupRequest;
import com.xdavide9.sso.config.SecurityConfig;
import com.xdavide9.sso.exception.authentication.api.IncorrectPasswordException;
import com.xdavide9.sso.exception.user.api.UserBannedException;
import com.xdavide9.sso.exception.user.fields.country.CountryNotFoundException;
import com.xdavide9.sso.jwt.JwtService;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.user.fields.country.Country;
import com.xdavide9.sso.user.fields.country.CountryRepository;
import com.xdavide9.sso.user.fields.country.CountryService;
import com.xdavide9.sso.util.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

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
     * Jpa repository to interact with the {@link User} table.
     */
    private final UserRepository userRepository;

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
    /**
     * It is used to interact with countries stored in the system
     */
    private final CountryRepository countryRepository;

    @Autowired
    public AuthenticationService(JwtService jwtService,
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 ValidatorService validatorService,
                                 @Qualifier(value = "repositoryUserDetailsService") UserDetailsService userDetailsService,
                                 CountryRepository countryRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validatorService = validatorService;
        this.userDetailsService = userDetailsService;
        this.countryRepository = countryRepository;
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
        String email = request.email();
        String rawPassword = request.password();
        validatorService.validateUsername(username);
        validatorService.validateEmail(email);
        validatorService.validateRawPassword(rawPassword);
        User user = new User(username, email, passwordEncoder.encode(rawPassword));
        // try to set additional fields
        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.gender() != null) user.setGender(request.gender());
        if (request.phoneNumber() != null) {
            String phoneNumber = request.phoneNumber();
            validatorService.validatePhoneNumber(phoneNumber);
            user.setPhoneNumber(phoneNumber);
        }
        if (request.dateOfBirth() != null) {
            LocalDate dateOfBirth = request.dateOfBirth();
            validatorService.validateDateOfBirth(dateOfBirth);
            user.setDateOfBirth(dateOfBirth);
        }
        if (request.country() != null) {
            Optional<Country> countryOption = countryRepository.findById(request.country());
            if (countryOption.isEmpty())
                throw new CountryNotFoundException(format("Country with code [%s] not found", request.country()));
            Country country = countryOption.get();
            user.setCountry(country);
        }
        userRepository.save(user);
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
