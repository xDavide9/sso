package com.xdavide9.sso.user;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * This class only tests the Builder pattern implemented in {@link User}
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
class UserTest {

    @Test
    void itShouldBuildProvidingCustomValuesForAllFields() {
        // given
        UUID uuid = UUID.randomUUID();
        String username = "Davide";
        String password = "123";
        String email = "davide@xdavide9.com";
        Role role = Role.ADMIN;
        boolean accountNonExpired = true;
        boolean accountNonLocked = false;
        boolean credentialsNonExpired = true;
        boolean enabled = true;
        // when
        User user = User.builder()
                .uuid(uuid)
                .username(username)
                .password(password)
                .email(email)
                .role(role)
                .accountNonExpired(accountNonExpired)
                .accountNonLocked(accountNonLocked)
                .credentialsNonExpired(credentialsNonExpired)
                .enabled(enabled)
                .build();
        // then
        assertThat(user.getUuid()).isEqualTo(uuid);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.isAccountNonExpired()).isEqualTo(accountNonExpired);
        assertThat(user.isAccountNonLocked()).isEqualTo(accountNonLocked);
        assertThat(user.isCredentialsNonExpired()).isEqualTo(credentialsNonExpired);
        assertThat(user.isEnabled()).isEqualTo(enabled);
        System.out.println(user);
    }

    @Test
    void itShouldBuildWithDefaults() {
        // can't test for the uuid as it is always randomly generated
        // given
        String username = "Davide";
        String password = "123";
        String email = "davide@xdavide9.com";
        Role role = Role.USER;
        boolean accountNonExpired = true;
        boolean accountNonLocked = true;
        boolean credentialsNonExpired = true;
        boolean enabled = true;
        // when
        User user = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
        // then
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.isAccountNonExpired()).isEqualTo(accountNonExpired);
        assertThat(user.isAccountNonLocked()).isEqualTo(accountNonLocked);
        assertThat(user.isCredentialsNonExpired()).isEqualTo(credentialsNonExpired);
        assertThat(user.isEnabled()).isEqualTo(enabled);
        System.out.println(user);
    }
}