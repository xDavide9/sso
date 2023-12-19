package com.xdavide9.sso.user;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

// tests if authorities are correctly created by the role of a user

@ActiveProfiles("test")
class RoleTest {

    static Stream<Arguments> roleAuthorityProvider() {
        return Stream.of(
                Arguments.of(
                        Role.ADMIN,
                        List.of(
                                "USER_GET", "USER_PUT",
                                "OPERATOR_GET", "OPERATOR_PUT",
                                "ADMIN_GET", "ADMIN_PUT", "ADMIN_DELETE", "ROLE_ADMIN"
                        )),
                Arguments.of(
                        Role.OPERATOR,
                        List.of(
                                "USER_GET", "USER_PUT",
                                "OPERATOR_GET", "OPERATOR_PUT", "ROLE_OPERATOR"
                        )),
                Arguments.of(
                        Role.USER,
                        List.of(
                                "USER_GET", "USER_PUT", "ROLE_USER"
                        ))
        );
    }

    @ParameterizedTest
    @MethodSource("roleAuthorityProvider")
    void itShouldGetAuthoritiesForRole(Role role, List<String> expectedPermissions) {
        // given
        List<SimpleGrantedAuthority> expectedAuthorities = expectedPermissions
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        // when
        List<SimpleGrantedAuthority> authorities = role.getAuthorities();
        // then
        assertThat(authorities).containsExactlyInAnyOrderElementsOf(expectedAuthorities);
    }
}