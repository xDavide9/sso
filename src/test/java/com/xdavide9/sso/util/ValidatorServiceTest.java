package com.xdavide9.sso.util;

import com.xdavide9.sso.user.PasswordDTO;
import com.xdavide9.sso.user.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ValidatorServiceTest {

    @InjectMocks
    private ValidatorService underTest;

    @Mock
    private Validator validator;

    @Test
    void itShouldValidateUserNoViolations() {
        // given
        User user = new User();
        given(validator.validate(user)).willReturn(new HashSet<>());
        // when
        underTest.validate(user);
        // then
        verify(validator).validate(user);

    }

    @Test
    void itShouldValidateUserThrowException() {
        // given
        User user = new User();
        Set<ConstraintViolation<User>> violations = new HashSet<>();
        ConstraintViolation<User> violation = mock(ConstraintViolation.class);
        violations.add(violation);
        given(validator.validate(user)).willReturn(violations);
        // when & then
        assertThatThrownBy(() -> underTest.validate(user))
                .isInstanceOf(ConstraintViolationException.class)
                .hasFieldOrPropertyWithValue("constraintViolations", violations);
    }

    @Test
    void itShouldValidatePasswordNoViolations() {
        // given
        PasswordDTO passwordDTO = new PasswordDTO("HelloWorld1!"); // > 8 characters, 1 special character, 1 upper case character
        given(validator.validate(passwordDTO)).willReturn(new HashSet<>());
        // when
        underTest.validate(passwordDTO);
        // then
        verify(validator).validate(passwordDTO);
    }

    @Test
    void itShouldValidatePasswordThrowException() {
        // given
        PasswordDTO passwordDTO = new PasswordDTO("Password");
        Set<ConstraintViolation<PasswordDTO>> violations = new HashSet<>();
        ConstraintViolation<PasswordDTO> violation = mock(ConstraintViolation.class);
        violations.add(violation);
        given(validator.validate(passwordDTO)).willReturn(violations);
        // when & then
        assertThatThrownBy(() -> underTest.validate(passwordDTO))
                .isInstanceOf(ConstraintViolationException.class)
                .hasFieldOrPropertyWithValue("constraintViolations", violations);
    }
}