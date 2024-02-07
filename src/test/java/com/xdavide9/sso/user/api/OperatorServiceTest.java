package com.xdavide9.sso.user.api;

import com.xdavide9.sso.exception.user.api.UserCannotBeModifiedException;
import com.xdavide9.sso.exception.user.api.UserExceptionReason;
import com.xdavide9.sso.exception.user.api.UserNotFoundException;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserDTO;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.util.TimeOutService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

// unit test for OperatorService

@ExtendWith(MockitoExtension.class)
class OperatorServiceTest {
    @InjectMocks
    private OperatorService underTest;
    @Mock
    private UserRepository repository;

    @Mock
    private TimeOutService timeOutService;

    @Test
    void itShouldGetUsers() {
        // given
        // when
        underTest.getUsers();
        // then
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void itShouldGetUserByUuid() {
        // given
        User user = new User();
        UUID uuid = user.getUuid();
        user.setUsername("usernameByUuid");
        given(repository.findById(uuid)).willReturn(Optional.of(user));
        // when
        UserDTO dto = underTest.getUserByUuid(uuid);
        // then
        assertThat(dto.getUsername()).isEqualTo("usernameByUuid");
    }

    @Test
    void itShouldNotGetUserByUuidAndThrow() {
        // given
        UUID uuid = UUID.randomUUID();
        given(repository.findById(uuid)).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> underTest.getUserByUuid(uuid))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(format("User with uuid [%s] not found.", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.INFORMATION);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void itShouldGetUserByUsername() {
        // given
        String username = "username";
        User user = new User();
        user.setUsername(username);
        given(repository.findByUsername(username)).willReturn(Optional.of(user));
        // when
        UserDTO dto = underTest.getUserByUsername(username);
        // then
        assertThat(dto.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void itShouldNotGetUserByUsernameAndThrow() {
        // given
        String username = "username";
        User user = new User();
        user.setUsername(username);
        given(repository.findByUsername(username)).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> underTest.getUserByUsername(username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(format(format("User with username [%s] not found.", username)))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.INFORMATION);
    }

    @Test
    void itShouldGetUserByEmail() {
        // given
        String email = "email@xdavide9.com";
        User user = new User();
        user.setEmail(email);
        given(repository.findByEmail(email)).willReturn(Optional.of(user));
        // when
        UserDTO dto = underTest.getUserByEmail(email);
        // then
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void itShouldNotGetUserByEmailAndThrow() {
        // given
        String email = "email@xdavide9.com";
        User user = new User();
        user.setEmail(email);
        given(repository.findByEmail(email)).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> underTest.getUserByEmail(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(format("User with email [%s] not found.", email))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.INFORMATION);
    }

    @Test
    void itShouldTimeOutCorrectlyWithDefaultDuration() {
        // given
        User user = new User();
        UUID uuid = UUID.randomUUID();
        given(repository.findById(uuid)).willReturn(Optional.of(user));
        // when
        ResponseEntity<String> response = underTest.timeOut(uuid, null);
        // then
        verify(timeOutService).timeOut(user);
        assertThat(response.getBody()).isEqualTo(format("User with uuid [%s] has been timed out for the default duration", uuid));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void itShouldTimeOutCorrectlyWithGivenDuration() {
        // given
        User user = new User();
        UUID uuid = UUID.randomUUID();
        given(repository.findById(uuid)).willReturn(Optional.of(user));
        long duration = 1000*60*60; // 1 hour
        // when
        ResponseEntity<String> response = underTest.timeOut(uuid, duration);
        // then
        verify(timeOutService).timeOut(user, duration);
        assertThat(response.getBody()).isEqualTo(format("User with uuid [%s] has been timed out for [%d] milliseconds", uuid, duration));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void itShouldNotTimeOutUserNotFound() {
        // given
        UUID uuid = UUID.randomUUID();
        // when & then
        assertThatThrownBy(() -> underTest.timeOut(uuid, null))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(format("User with uuid [%s] not found", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.TIMEOUT);
    }

    @Test
    void itShouldNotTimeOutUserIsAlreadyDisabled() {
        // given
        User user = new User();
        user.setEnabled(false);
        UUID uuid = UUID.randomUUID();
        given(repository.findById(uuid)).willReturn(Optional.of(user));
        // when & then
        assertThatThrownBy(() -> underTest.timeOut(uuid, null))
                .isInstanceOf(UserCannotBeModifiedException.class)
                .hasMessageContaining(format("User with uuid [%s] is already banned or timed out", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.TIMEOUT);
    }
}