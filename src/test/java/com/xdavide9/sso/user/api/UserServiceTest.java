package com.xdavide9.sso.user.api;

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
class UserServiceTest {
    private UserService underTest;
    @Mock
    private UserRepository repository;


    @BeforeEach
    void setUp() {
        underTest = new UserService(repository);
    }

    /**
     * happy path
     * @author xdavide9
     * @since 0.0.1-SNAPSHOT
     * @see UserService#getUsers()
     */
    @Test
    void itShouldGetUsers() {
        // given
        // when
        underTest.getUsers();
        // then
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    /**
     * happy path
     * @author xdavide9
     * @since 0.0.1-SNAPSHOT
     * @see UserService#getUserByUuid(UUID)
     */
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
    }

    /**
     * should throw custom exception
     * @author xdavide9
     * @since 0.0.1-SNAPSHOT
     * @see UserService#getUserByUuid(UUID)
     */
    @Test
    void itShouldNotGetUserByUuidAndThrow() {
        // given
        UUID uuid = UUID.randomUUID();
        given(repository.findById(uuid)).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> underTest.getUserByUuid(uuid))
                .isInstanceOf(IllegalArgumentException.class);

    }
}