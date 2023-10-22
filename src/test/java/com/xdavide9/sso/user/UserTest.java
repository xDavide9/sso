package com.xdavide9.sso.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private Validator validator;
    private User user;

    @BeforeEach
    void setUp() {
        validator = buildDefaultValidatorFactory().getValidator();
        user = new User();
    }

    @Test
    void itShouldCreateValidUser() {
        // given
        // when
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email@xdavide9.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        // then
        assertThat(violations).isEmpty();
    }

    static Stream<Arguments> userFieldsProvider() {
        return Stream.of(
                Arguments.of(
                        null, "password", "email@email.com",
                        Role.USER, "Username cannot be blank nor null"
                ),
                Arguments.of(
                        "", "password", "email@email.com",
                        Role.USER, "Username cannot be blank nor null"
                ),
                Arguments.of(
                        "username", null, "email@email.com",
                        Role.USER, "Password cannot be blank nor null"
                ),
                Arguments.of(
                        "username", "", "email@email.com",
                        Role.USER, "Password cannot be blank nor null"
                ),
                Arguments.of(
                        "username", "password", "emailemail.com",
                        Role.USER, "Invalid email format"
                ),
                Arguments.of(
                        "username", "password", "email@email.com",
                        null, "Role cannot be null"
                )
        );
    }


    @ParameterizedTest
    @MethodSource("userFieldsProvider")
    void itShouldNotCreateUser(String username,
                               String password,
                               String email,
                               Role role,
                               String errorMessage) {
        // given
        // when
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(role);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        // then
        assertThat(violations).isNotEmpty();
        ConstraintViolation<User> violation = violations.iterator().next();
        assertThat(violation.getMessage().contains(errorMessage)).isTrue();
    }

}