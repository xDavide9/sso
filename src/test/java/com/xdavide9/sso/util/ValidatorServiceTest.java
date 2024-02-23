package com.xdavide9.sso.util;

import com.xdavide9.sso.user.PasswordDTO;
import com.xdavide9.sso.user.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidatorServiceTest {

    @InjectMocks
    @Spy
    private ValidatorService underTest;

    @Mock
    private Validator validator;

    @Captor
    private ArgumentCaptor<PasswordDTO> dtoCaptor;

    @Test
    void itShouldValidateUserNoViolations() {
        // given
        User user = new User();
        given(validator.validate(user)).willReturn(new HashSet<>());
        // when
        underTest.validateUser(user);
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
        assertThatThrownBy(() -> underTest.validateUser(user))
                .isInstanceOf(ConstraintViolationException.class)
                .hasFieldOrPropertyWithValue("constraintViolations", violations);
    }

    @Test
    void itShouldValidatePasswordNoViolations() {
        // given
        String password = "ValidPassword!";
        given(validator.validate(any(PasswordDTO.class))).willReturn(new HashSet<>());
        // when
        underTest.validateRawPassword(password);
        // then
        verify(underTest).validateUsingJakartaConstraints(dtoCaptor.capture());
        assertThat(dtoCaptor.getValue().getPassword()).isEqualTo(password);
    }

    @Test
    void itShouldValidatePasswordThrowException() {
        // given
        String password = "ValidPassword!";
        ConstraintViolation<PasswordDTO> violation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<PasswordDTO>> violations = new HashSet<>();
        violations.add(violation);
        given(validator.validate(any(PasswordDTO.class))).willReturn(violations);
        // when & then
        assertThatThrownBy(() -> underTest.validateRawPassword(password))
                .isInstanceOf(ConstraintViolationException.class)
                .hasFieldOrPropertyWithValue("constraintViolations", violations);
    }
}