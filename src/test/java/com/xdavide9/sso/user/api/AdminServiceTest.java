package com.xdavide9.sso.user.api;

import com.xdavide9.sso.exception.user.api.UserCannotBeModifiedException;
import com.xdavide9.sso.exception.user.api.UserExceptionReason;
import com.xdavide9.sso.exception.user.api.UserNotFoundException;
import com.xdavide9.sso.user.Role;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

// unit test for AdminService
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService underTest;
    @Mock
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<User> captor;
    @Test
    void itShouldPromoteUserToOperator() {
        // given
        User user = new User("username", "email@email.com", "password");
        UUID uuid = user.getUuid();
        given(userRepository.findById(uuid)).willReturn(Optional.of(user));
        // when
        ResponseEntity<String> response = underTest.promoteUserToOperator(uuid);
        // then
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(Role.OPERATOR);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(format("The user [%s] has been successfully promoted to operator", uuid));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void itShouldNotPromoteUserToOperatorUserDoesNotExistInDb() {
        // given
        UUID uuid = UUID.randomUUID();
        given(userRepository.findById(uuid)).willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> underTest.promoteUserToOperator(uuid))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(format("Could not find user with uuid [%s] to be promoted to Operator", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.PROMOTION);
    }

    @Test
    void itShouldNotPromoteUserToOperatorUserDoesNotHaveRoleUser() {
        // given
        User user = new User("username", "email@email.com", "password");
        user.setRole(Role.OPERATOR);
        UUID uuid = user.getUuid();
        given(userRepository.findById(uuid)).willReturn(Optional.of(user));
        // when & then
        assertThatThrownBy(() -> underTest.promoteUserToOperator(uuid))
                .isInstanceOf(UserCannotBeModifiedException.class)
                .hasMessageContaining(format("Could not promote user [%s] because they do not have USER role", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.PROMOTION);
    }

    @Test
    void itShouldDeleteUser() {
        // given
        User user = new User("username", "email@email.com", "password");
        UUID uuid = user.getUuid();
        given(userRepository.findById(uuid)).willReturn(Optional.of(user));
        // when
        ResponseEntity<String> response = underTest.deleteUser(uuid);
        // then
        verify(userRepository).delete(captor.capture());
        User capturedUser = captor.getValue();
        assertThat(capturedUser).isEqualTo(user);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(format("The user [%s] has been permanently deleted from the system", uuid));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void itShouldNotDeleteUserDoesNotExist() {
        // given
        UUID uuid = UUID.randomUUID();
        given(userRepository.findById(uuid)).willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> underTest.deleteUser(uuid))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(format("Could not find user with uuid [%s] to be deleted", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.DELETION);
    }

    @Test
    void itShouldNotDeleteUserIsAnAdmin() {
        // given
        User user = new User("username", "email@email.com", "password");
        user.setRole(Role.ADMIN);
        UUID uuid = user.getUuid();
        given(userRepository.findById(uuid)).willReturn(Optional.of(user));
        // when & then
        assertThatThrownBy(() -> underTest.deleteUser(uuid))
                .isInstanceOf(UserCannotBeModifiedException.class)
                .hasMessageContaining(format("Could not delete user [%s] because they are an admin", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.DELETION);
    }

    @Test
    void itShouldDemoteUser() {
        // given
        User user = new User("username", "email@email.com", "password");
        user.setRole(Role.OPERATOR);
        UUID uuid = user.getUuid();
        given(userRepository.findById(uuid)).willReturn(Optional.of(user));
        // when
        ResponseEntity<String> response = underTest.demoteUser(uuid);
        // then
        verify(userRepository).save(captor.capture());
        User capturedUser = captor.getValue();
        assertThat(capturedUser.getRole()).isEqualTo(Role.USER);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(format("The user [%s] has been demoted to a plain user", uuid));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void itShouldNotDemoteUserNotFound() {
        // given
        UUID uuid = UUID.randomUUID();
        given(userRepository.findById(uuid)).willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> underTest.demoteUser(uuid))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(format("Could not find user with uuid [%s] to be demoted to a plain user", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.DEMOTION);
    }

    @Test
    void itShouldNotDemoteUserIsNotOperator() {
        // given
        User user = new User("username", "email@email.com", "password");
        UUID uuid = user.getUuid();
        given(userRepository.findById(uuid)).willReturn(Optional.of(user));
        // when & then
        assertThatThrownBy(() -> underTest.demoteUser(uuid))
                .isInstanceOf(UserCannotBeModifiedException.class)
                .hasMessageContaining(format("Could not demote user [%s] because they are not an operator", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.DEMOTION);
    }
}