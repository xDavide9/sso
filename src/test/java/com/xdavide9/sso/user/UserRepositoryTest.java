package com.xdavide9.sso.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Tests running against sso_user table in public schema using h2 autoconfigured in memory database
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 * @see UserRepository
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;
    private UUID uuid;
    private String username, email;
    private User user;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        username = "xdavide9";
        email = "davide@xdavide9.com";
        user = User.builder()
                .uuid(uuid)
                .username(username)
                .password("123")
                .email(email)
                .build();
    }

    @Test
    void itShouldSaveUser() {
        // given
        // when
        underTest.save(user);
        // then
        assertThat(underTest.findById(uuid)).isPresent()
                .hasValueSatisfying(u -> assertThat(u).isEqualTo(user));
    }

    @Test
    void itShouldFindByUsername() {
        // given
        // when
        underTest.save(user);
        // then
        assertThat(underTest.findByUsername(username)).isPresent()
                .hasValueSatisfying(u -> assertThat(u).isEqualTo(user));
    }

    @Test
    void itShouldFindByEmail() {
        // given
        // when
        underTest.save(user);
        // then
        assertThat(underTest.findByEmail(email)).isPresent()
                .hasValueSatisfying(u -> assertThat(u).isEqualTo(user));
    }
}