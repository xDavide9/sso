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

    }

    @Test
    void itShouldNotGetUserByUsernameAndThrow() {

    }

    @Test
    void itShouldGetUserByEmail() {

    }

    @Test
    void itShouldNotGetUserByEmailAndThrow() {

    }
}