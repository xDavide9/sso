package com.xdavide9.sso.user.api;

import com.xdavide9.sso.exception.user.api.UserCannotBeModifiedException;
import com.xdavide9.sso.exception.user.api.UserExceptionReason;
import com.xdavide9.sso.exception.user.api.UserNotFoundException;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.user.fields.role.Role;
import com.xdavide9.sso.util.UserModifierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

// unit test for AdminService
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService underTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserModifierService userModifierService;
    @Test
    void itShouldPromoteUserToOperator() {
        // given
        User user = new User("username", "email@email.com", "password");
        UUID uuid = user.getUuid();
        given(userRepository.findById(uuid)).willReturn(Optional.of(user));
        // when
        ResponseEntity<String> response = underTest.promoteUserToOperator(uuid);
        // then
        verify(userModifierService).setRole(user, Role.OPERATOR);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(format("The user [%s] has been successfully promoted to operator", uuid));
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
    void itShouldBanUser() {
        // given
        User user = new User();
        UUID uuid = user.getUuid();
        given(userRepository.findById(uuid)).willReturn(Optional.of(user));
        // when
        ResponseEntity<String> response = underTest.banUser(uuid);
        // then
        verify(userModifierService).setEnabled(user, false);
        verify(userModifierService).setDisabledUntil(user, LocalDateTime.of(3000, 1, 1, 1,1));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(format("The user [%s] has been successfully banned from the system", uuid));
    }

    @Test
    void itShouldNotBanUserDoesNotExist() {
        // given
        UUID uuid = UUID.randomUUID();
        given(userRepository.findById(uuid)).willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> underTest.banUser(uuid))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(format("Could not find user with uuid [%s] to be banned", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.BAN);
    }

    @Test
    void itShouldNotBanUserIsAnAdmin() {
        // given
        User user = new User("username", "email@email.com", "password");
        user.setRole(Role.ADMIN);
        UUID uuid = user.getUuid();
        given(userRepository.findById(uuid)).willReturn(Optional.of(user));
        // when & then
        assertThatThrownBy(() -> underTest.banUser(uuid))
                .isInstanceOf(UserCannotBeModifiedException.class)
                .hasMessageContaining(format("Could not ban user [%s] because they are an admin", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.BAN);
    }

    @Test
    void itShouldNotBanUserIsAlreadyBanned() {
        // given
        User user = new User();
        user.setEnabled(false);
        UUID uuid = user.getUuid();
        given(userRepository.findById(uuid)).willReturn(Optional.of(user));
        // when & then
        assertThatThrownBy(() -> underTest.banUser(uuid))
                .isInstanceOf(UserCannotBeModifiedException.class)
                .hasMessageContaining(format("Could not ban user [%s] because they are already banned", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.BAN);
    }

    @Test
    void itShouldUnbanUser() {
        // given
        User user = new User();
        user.setEnabled(false);
        UUID uuid = user.getUuid();
        given(userRepository.findById(uuid)).willReturn(Optional.of(user));
        // when
        ResponseEntity<String> response = underTest.unbanUser(uuid);
        // then
        verify(userModifierService).setEnabled(user, true);
        verify(userModifierService).setDisabledUntil(user, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(format("The user [%s] has been successfully unbanned", uuid));
    }

    @Test
    void itShouldNotUnbanUserDoesNotExist() {
        // given
        UUID uuid = UUID.randomUUID();
        // when & then
        assertThatThrownBy(() -> underTest.unbanUser(uuid))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(format("Could not find user with uuid [%s] to be unbanned", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.UNBAN);
    }

    @Test
    void itShouldNotUnbanUserNotBannedInTheFirstPlace() {
        // given
        User user = new User();
        UUID uuid = user.getUuid();
        given(userRepository.findById(uuid)).willReturn(Optional.of(user));
        // when & then
        assertThatThrownBy(() -> underTest.unbanUser(uuid))
                .isInstanceOf(UserCannotBeModifiedException.class)
                .hasMessageContaining(format("Could not unban user [%s] because they are not banned", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.UNBAN);
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
        verify(userModifierService).setRole(user, Role.USER);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(format("The user [%s] has been demoted to a plain user", uuid));
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