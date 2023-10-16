package com.xdavide9.sso.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// tests running against sso_user table in public schema using h2 autoconfigured in memory database

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    @Test
    void itShouldSaveUser() {
        // given
        UUID uuid = UUID.randomUUID();
        String username = "davide";
        String password = "123";
        String email = "davide@xdavide9.com";
        User user = User.builder()
                .uuid(uuid)
                .username(username)
                .password(password)
                .email(email)
                .build();
        // when
        underTest.save(user);
        // then
        assertThat(underTest.findById(uuid)).isPresent()
                .hasValueSatisfying(u -> assertThat(u).isEqualTo(user));
    }
}