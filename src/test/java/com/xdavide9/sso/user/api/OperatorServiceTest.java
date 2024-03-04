package com.xdavide9.sso.user.api;

import com.xdavide9.sso.exception.authentication.api.EmailTakenException;
import com.xdavide9.sso.exception.authentication.api.UsernameTakenException;
import com.xdavide9.sso.exception.user.api.UserCannotBeModifiedException;
import com.xdavide9.sso.exception.user.api.UserExceptionReason;
import com.xdavide9.sso.exception.user.api.UserNotFoundException;
import com.xdavide9.sso.user.fields.role.Role;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.util.TimeOutService;
import com.xdavide9.sso.util.UserModifierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

// unit test for OperatorService

@ExtendWith(MockitoExtension.class)
class OperatorServiceTest {
    @InjectMocks
    @Spy
    private OperatorService underTest;
    @Mock
    private UserRepository repository;

    @Mock
    private TimeOutService timeOutService;

    @Mock
    private UserModifierService userModifierService;


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
        User returnedUser = underTest.getUserByUuid(uuid);
        // then
        assertThat(returnedUser.getUsername()).isEqualTo("usernameByUuid");
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
        User returnedUser = underTest.getUserByUsername(username);
        // then
        assertThat(returnedUser.getUsername()).isEqualTo(user.getUsername());
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
        User returnedUser = underTest.getUserByEmail(email);
        // then
        assertThat(returnedUser.getEmail()).isEqualTo(user.getEmail());
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

    @ParameterizedTest
    @CsvSource({
            "OPERATOR,USER",
            "ADMIN,USER",
            "ADMIN,OPERATOR",
            "ADMIN,ADMIN",
    })
    void itShouldTimeOutCorrectlyWithDefaultDurationAndAllAllowedRolesCombination(String changerRole, String changedRole) {
        // given
        String username = "username";
        UUID uuid = UUID.randomUUID();
        User changer = new User();
        User changed = new User();
        changer.setRole(Role.valueOf(changerRole));
        changed.setRole(Role.valueOf(changedRole));
        setPrincipal(changer);
        given(repository.findById(uuid)).willReturn(Optional.of(changed));
        // when
        underTest.changeUsername(uuid, username);
        // then
        verify(userModifierService).setUsername(changed, username);
    }

    @Test
    void itShouldTimeOutCorrectlyWithGivenDuration() {
        // given
        User user = new User();
        User operator = new User();
        operator.setRole(Role.OPERATOR);
        setPrincipal(operator);
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
        User operator = new User();
        operator.setRole(Role.OPERATOR);
        setPrincipal(operator);
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
        User operator = new User();
        operator.setRole(Role.OPERATOR);
        setPrincipal(operator);
        user.setEnabled(false);
        UUID uuid = UUID.randomUUID();
        given(repository.findById(uuid)).willReturn(Optional.of(user));
        // when & then
        assertThatThrownBy(() -> underTest.timeOut(uuid, null))
                .isInstanceOf(UserCannotBeModifiedException.class)
                .hasMessageContaining(format("User with uuid [%s] is already banned or timed out", uuid))
                .hasFieldOrPropertyWithValue("reason", UserExceptionReason.TIMEOUT);
    }
    @ParameterizedTest
    @CsvSource({
            "OPERATOR,OPERATOR",
            "OPERATOR,ADMIN"
    })
    void itShouldNotTimeoutAccessDenied(String changerRole, String changedRole) {
        // given
        UUID uuid = UUID.randomUUID();
        User changer = new User();
        User changed = new User();
        changed.setEnabled(true);
        changer.setRole(Role.valueOf(changerRole));
        changed.setRole(Role.valueOf(changedRole));
        setPrincipal(changer);
        given(repository.findById(uuid)).willReturn(Optional.of(changed));
        // when & then
        assertThatThrownBy(() -> underTest.timeOut(uuid, null))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining(format("Access Denied. You cannot time out" +
                        " user with uuid [%s] because they are an operator or admin.", uuid));
    }

    private void setPrincipal(User user) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        given(underTest.securityContext()).willReturn(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(user);
    }

    // taking into consideration that user can never run this code as security blocks them
    @ParameterizedTest
    @CsvSource({
            "OPERATOR,USER",
            "ADMIN,USER",
            "ADMIN,OPERATOR",
            "ADMIN,ADMIN",
    })
    void itShouldChangeUsername(String changerRole, String changedRole) {
        // given
        String username = "username";
        UUID uuid = UUID.randomUUID();
        User changer = new User();
        User changed = new User();
        changer.setRole(Role.valueOf(changerRole));
        changed.setRole(Role.valueOf(changedRole));
        setPrincipal(changer);
        given(repository.findById(uuid)).willReturn(Optional.of(changed));
        // when
        underTest.changeUsername(uuid, username);
        // then
        verify(userModifierService).setUsername(changed, username);
    }

    @Test
    void itShouldNotChangeUsernameBecauseItIsTaken() {
        // given
        String username = "username";
        User user = new User();
        UUID uuid = UUID.randomUUID();
        User operator = new User();
        operator.setRole(Role.OPERATOR);
        setPrincipal(operator);
        given(repository.findById(uuid)).willReturn(Optional.of(user));
        given(repository.existsByUsername(username)).willReturn(true);
        // when & then
        assertThatThrownBy(() -> underTest.changeUsername(uuid, username))
                .isInstanceOf(UsernameTakenException.class)
                .hasMessageContaining(format("Cannot change username of user with uuid [%s] because it is taken", uuid));
    }

    @ParameterizedTest
    @CsvSource({
            "OPERATOR,OPERATOR",
            "OPERATOR,ADMIN"
    })
    void itShouldNotChangeUsernameAccessDenied(String changerRole, String changedRole) {
        // given
        // operator2 tries to change username of operator1
        String username = "username";
        UUID uuid = UUID.randomUUID();
        User changer = new User();
        User changed = new User();
        changer.setRole(Role.valueOf(changerRole));
        changed.setRole(Role.valueOf(changedRole));
        setPrincipal(changer);
        given(repository.findById(uuid)).willReturn(Optional.of(changed));
        // when & then
        assertThatThrownBy(() -> underTest.changeUsername(uuid, username))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining(format("Access Denied. You cannot change the username of user with uuid [%s] because they are an operator or admin", uuid));
    }

    @ParameterizedTest
    @CsvSource({
            "OPERATOR,USER",
            "ADMIN,USER",
            "ADMIN,OPERATOR",
            "ADMIN,ADMIN",
    })
    void itShouldChangeEmail(String changerRole, String changedRole) {
        // given
        String email = "email@email.com";
        UUID uuid = UUID.randomUUID();
        User changer = new User();
        User changed = new User();
        changer.setRole(Role.valueOf(changerRole));
        changed.setRole(Role.valueOf(changedRole));
        setPrincipal(changer);
        given(repository.findById(uuid)).willReturn(Optional.of(changed));
        // when
        underTest.changeEmail(uuid, email);
        // then
        verify(userModifierService).setEmail(changed, email);
    }

    @Test
    void itShouldNotChangeEmailBecauseItIsTaken() {
        // given
        String email = "email@email.com";
        User user = new User();
        UUID uuid = UUID.randomUUID();
        User operator = new User();
        operator.setRole(Role.OPERATOR);
        given(repository.findById(uuid)).willReturn(Optional.of(user));
        given(repository.existsByEmail(email)).willReturn(true);
        setPrincipal(operator);
        // when & then
        assertThatThrownBy(() -> underTest.changeEmail(uuid, email))
                .isInstanceOf(EmailTakenException.class)
                .hasMessageContaining(format("Cannot change email of user with uuid [%s] because it is taken", uuid));
    }

    @ParameterizedTest
    @CsvSource({
            "OPERATOR,OPERATOR",
            "OPERATOR,ADMIN"
    })
    void itShouldNotChangeEmailAccessDenied(String changerRole, String changedRole) {
        // given
        // operator2 tries to change username of operator1
        String username = "username";
        UUID uuid = UUID.randomUUID();
        User changer = new User();
        User changed = new User();
        changer.setRole(Role.valueOf(changerRole));
        changed.setRole(Role.valueOf(changedRole));
        setPrincipal(changer);
        given(repository.findById(uuid)).willReturn(Optional.of(changed));
        // when & then
        assertThatThrownBy(() -> underTest.changeEmail(uuid, username))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining(format("Access Denied. You cannot change the email of user with uuid [%s] because they are an operator or admin", uuid));
    }
}