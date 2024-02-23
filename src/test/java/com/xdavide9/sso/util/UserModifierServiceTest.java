package com.xdavide9.sso.util;

import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserModifierServiceTest {

    @InjectMocks
    private UserModifierService underTest;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ValidatorService validatorService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void itShouldSetUsernameCorrectly() {
        // given
        User user = new User();
        String username = "username";
        // when
        underTest.setUsername(user, username);
        // then
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        verify(validatorService).validateUser(captured);
        verify(validatorService).validateUsername(username);
        assertThat(captured.getUsername()).isEqualTo(username);
    }

    @Test
    void itShouldSetEmailCorrectly() {
        // given
        User user = new User();
        String email = "emailgmail.com";
        // when
        underTest.setEmail(user, email);
        // then
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        verify(validatorService).validateUser(captured);
        verify(validatorService).validateEmail(email);
        assertThat(captured.getEmail()).isEqualTo(email);
    }

    @Test
    void itShouldSetPasswordCorrectly() {
        // given
        User user = new User();
        String password = "ValidPass1!";
        given(passwordEncoder.encode(password)).willReturn("encoded");
        // when
        underTest.setPassword(user, password);
        // then
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        verify(validatorService).validateUser(captured);
        verify(validatorService).validateRawPassword(password);
        assertThat(captured.getPassword()).isEqualTo("encoded");
    }

    @Test
    void itShouldSetPhoneNumberCorrectly() {
        // given
        User user = new User();
        String phoneNumber = "+393339977000";
        // when
        underTest.setPhoneNumber(user, phoneNumber);
        // then
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        verify(validatorService).validateUser(captured);
        verify(validatorService).validatePhoneNumber(phoneNumber);
        assertThat(captured.getPhoneNumber()).isEqualTo(phoneNumber);
    }
}