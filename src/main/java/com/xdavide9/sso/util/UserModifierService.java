package com.xdavide9.sso.util;

import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.user.change.UserChange;
import com.xdavide9.sso.user.change.UserChangeRepository;
import com.xdavide9.sso.user.fields.Gender;
import com.xdavide9.sso.user.fields.UserField;
import com.xdavide9.sso.user.fields.country.Country;
import com.xdavide9.sso.user.fields.country.CountryService;
import com.xdavide9.sso.user.fields.role.Role;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;

/**
 * This service contains methods that change {@link User}s attributes.
 * It does so by first validating them using {@link ValidatorService} in different ways
 * and then saving to db with {@link UserRepository}.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
@Service
public class UserModifierService {
    private final UserRepository userRepository;
    private final UserChangeRepository userChangeRepository;
    private final CountryService countryService;
    private final ValidatorService validatorService;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserModifierService(UserRepository userRepository,
                               ValidatorService validatorService,
                               PasswordEncoder passwordEncoder,
                               UserChangeRepository userChangeRepository, CountryService countryService) {
        this.userRepository = userRepository;
        this.validatorService = validatorService;
        this.passwordEncoder = passwordEncoder;
        this.userChangeRepository = userChangeRepository;
        this.countryService = countryService;
    }

    private <T> void setAttribute(User user, T value, BiConsumer<User, T> setter) {
        setter.accept(user, value);
        userRepository.save(user);
    }

    @Transactional
    public void setUsername(User user, String username) {
        validatorService.validateUsername(username);
        userChangeRepository.save(new UserChange(
                user,
                UserField.USERNAME,
                user.getUsername(),
                username
        ));
        setAttribute(user, username, User::setUsername);
    }

    @Transactional
    public void setEmail(User user, String email) {
        validatorService.validateEmail(email);
        userChangeRepository.save(new UserChange(
                user,
                UserField.EMAIL,
                user.getEmail(),
                email
        ));
        setAttribute(user, email, User::setEmail);
    }

    @Transactional
    public void setPassword(User user, String password) {
        validatorService.validateRawPassword(password);
        userChangeRepository.save(new UserChange(
                user,
                UserField.PASSWORD,
                null,
                null
        ));
        setAttribute(user, passwordEncoder.encode(password), User::setPassword);
    }

    @Transactional
    public void setPhoneNumber(User user, String phoneNumber) {
        validatorService.validatePhoneNumber(phoneNumber);
        userChangeRepository.save(new UserChange(
                user,
                UserField.PHONE_NUMBER,
                user.getPhoneNumber(),
                phoneNumber
        ));
        setAttribute(user, phoneNumber, User::setPhoneNumber);
    }

    @Transactional
    public void setCountry(User user, String countryCode) {
        Country country = countryService.getCountry(countryCode);
        String previous = null;
        if (user.getCountry() != null)
            previous = user.getCountry().toString();
        userChangeRepository.save(new UserChange(
                user,
                UserField.COUNTRY,
                previous,
                country.toString()
        ));
        setAttribute(user, country, User::setCountry);
    }

    @Transactional
    public void setDateOfBirth(User user, LocalDate dateOfBirth) {
        validatorService.validateDateOfBirth(dateOfBirth);
        String previous = null;
        if (user.getDateOfBirth() != null)
            previous = user.getDateOfBirth().toString();
        userChangeRepository.save(new UserChange(
                user,
                UserField.DATE_OF_BIRTH,
                previous,
                dateOfBirth.toString()
        ));
        setAttribute(user, dateOfBirth, User::setDateOfBirth);
    }

    @Transactional
    public void setFirstName(User user, String firstName) {
        userChangeRepository.save(new UserChange(
                user,
                UserField.FIRST_NAME,
                user.getFirstName(),
                firstName
        ));
        setAttribute(user, firstName, User::setFirstName);
    }

    @Transactional
    public void setLastName(User user, String lastName) {
        userChangeRepository.save(new UserChange(
                user,
                UserField.LAST_NAME,
                user.getLastName(),
                lastName
        ));
        setAttribute(user, lastName, User::setLastName);
    }

    @Transactional
    public void setGender(User user, Gender gender) {
        String previous = null;
        if (user.getGender() != null)
            previous = user.getGender().toString();
        userChangeRepository.save(new UserChange(
                user,
                UserField.GENDER,
                previous,
                gender.toString()
        ));
        setAttribute(user, gender, User::setGender);
    }

    @Transactional
    public void setRole(User user, Role role) {
        userChangeRepository.save(new UserChange(
                user,
                UserField.ROLE,
                user.getRole().toString(),
                role.toString()
        ));
        setAttribute(user, role, User::setRole);
    }

    @Transactional
    public void setEnabled(User user, Boolean enabled) {
        userChangeRepository.save(new UserChange(
                user,
                UserField.ENABLED,
                Boolean.toString(user.isEnabled()),
                enabled.toString()
        ));
        setAttribute(user, enabled, User::setEnabled);
    }

    @Transactional
    public void setDisabledUntil(User user, LocalDateTime disabledUntil) {
        String previous = null;
        if (user.getDisabledUntil() != null)
            previous = user.getDisabledUntil().toString();
        if (disabledUntil == null) {
            userChangeRepository.save(new UserChange(
                    user,
                    UserField.DISABLED_UNTIL,
                    previous,
                    null
            ));
        } else {
            userChangeRepository.save(new UserChange(
                    user,
                    UserField.DISABLED_UNTIL,
                    previous,
                    disabledUntil.toString()
            ));
        }

        setAttribute(user, disabledUntil, User::setDisabledUntil);
    }

    @Transactional
    public void setAccountNonExpired(User user, Boolean accountNonExpired) {
        userChangeRepository.save(new UserChange(
                user,
                UserField.ACCOUNT_NON_EXPIRED,
                Boolean.toString(user.isAccountNonExpired()),
                accountNonExpired.toString()
        ));
        setAttribute(user, accountNonExpired, User::setAccountNonExpired);
    }

    @Transactional
    public void setCredentialsNonExpired(User user, Boolean credentialsNonExpired) {
        userChangeRepository.save(new UserChange(
                user,
                UserField.CREDENTIALS_NON_EXPIRED,
                Boolean.toString(user.isCredentialsNonExpired()),
                credentialsNonExpired.toString()
        ));
        setAttribute(user, credentialsNonExpired, User::setCredentialsNonExpired);
    }

    @Transactional
    public void setAccountNonLocked(User user, Boolean accountNonLocked) {
        userChangeRepository.save(new UserChange(
                user,
                UserField.ACCOUNT_NON_LOCKED,
                Boolean.toString(user.isAccountNonLocked()),
                accountNonLocked.toString()
        ));
        setAttribute(user, accountNonLocked, User::setAccountNonLocked);
    }
}
