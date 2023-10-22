package com.xdavide9.sso.user.api;

import com.xdavide9.sso.exception.UserNotFoundException;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
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
        UUID uuid = UUID.randomUUID();
        User user = User.builder()
                .username("david")
                .password("123")
                .email("david@xdavide9.com")
                .build();
        Optional<User> userOptional = Optional.of(user);
        given(repository.findById(uuid)).willReturn(userOptional);
        // when
        User returnedUser = underTest.getUserByUuid(uuid);
        // then
        assertThat(userOptional).isPresent()
                .hasValueSatisfying(u -> assertThat(u).isEqualTo(returnedUser));
        verifyNoMoreInteractions(repository);
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
                .hasMessageContaining(String.format("User with uuid [%s] not found.", uuid));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void itShouldGetUserByUsername() {
        // given
        String username = "david";
        User user = User.builder()
                .username(username)
                .password("123")
                .email("david@xdavide9.com")
                .build();
        Optional<User> userOptional = Optional.of(user);
        given(repository.findByUsername(username)).willReturn(userOptional);
        // when
        User returnedUser = underTest.getUserByUsername(username);
        // then
        assertThat(userOptional).isPresent()
                .hasValueSatisfying(u -> assertThat(u).isEqualTo(returnedUser));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void itShouldNotGetUserByUsernameAndThrow() {
        // given
        String username = "david";
        given(repository.findByUsername(username)).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> underTest.getUserByUsername(username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(String.format("User with username [%s] not found.", username));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void itShouldGetUserByEmail() {
        // given
        String email = "david@xdavide9.com";
        User user = User.builder()
                .username("david")
                .password("123")
                .email(email)
                .build();
        Optional<User> userOptional = Optional.of(user);
        given(repository.findByEmail(email)).willReturn(userOptional);
        // when
        User returnedUser = underTest.getUserByEmail(email);
        // then
        assertThat(userOptional).isPresent()
                .hasValueSatisfying(u -> assertThat(u).isEqualTo(returnedUser));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void itShouldNotGetUserByEmailAndThrow() {
        // given
        String email = "david@xdavide9.com";
        given(repository.findByEmail(email)).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> underTest.getUserByEmail(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(String.format("User with email [%s] not found.", email));
        verifyNoMoreInteractions(repository);
    }
}