package com.xdavide9.sso.user.api;

import com.xdavide9.sso.exception.user.api.UserExceptionReason;
import com.xdavide9.sso.exception.user.api.UserNotFoundException;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserDTO;
import com.xdavide9.sso.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

// unit test for OperatorService

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OperatorServiceTest {
    private OperatorService underTest;
    @Mock
    private UserRepository repository;

    @BeforeEach
    void setUp() {
        underTest = new OperatorService(repository);
    }

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
                .hasMessageContaining(String.format("User with uuid [%s] not found.", uuid))
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
                .hasMessageContaining(String.format(String.format("User with username [%s] not found.", username)))
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
                .hasMessageContaining(String.format("User with email [%s] not found.", email))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.INFORMATION);
    }
}