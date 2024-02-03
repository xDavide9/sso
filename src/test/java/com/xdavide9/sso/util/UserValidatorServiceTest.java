package com.xdavide9.sso.util;

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
class UserValidatorServiceTest {

    @InjectMocks
    private UserValidatorService underTest;

    @Mock
    private Validator validator;

    @Test
    void itShouldValidateNoViolations() {
        // given
        User user = new User();
        given(validator.validate(user)).willReturn(new HashSet<>());
        // when
        underTest.validate(user);
        // then
        verify(validator).validate(user);

    }

    @Test
    void itShouldValidateThrowException() {
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
}