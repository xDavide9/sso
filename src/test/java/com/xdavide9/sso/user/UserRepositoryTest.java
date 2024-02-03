package com.xdavide9.sso.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


 // unit tests running against sso_user table in public schema using h2 autoconfigured in memory database for UserRepository
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    @Test
    void itShouldSaveUser() {
        // given
        User user = new User();
        user.setUsername("xdavide9");
        user.setPassword("password");
        user.setEmail("valid@email.com");
        // when
        underTest.save(user);
        // then
        assertThat(underTest.findById(user.getUuid())).isPresent()
                .hasValueSatisfying(u -> assertThat(u).isEqualTo(user));
    }

    @Test
    void itShouldFindByUsername() {
        // given
        User user = new User();
        user.setUsername("xdavide9");
        user.setPassword("password");
        user.setEmail("valid@email.com");
        // when
        underTest.save(user);
        // then
        assertThat(underTest.findByUsername("xdavide9")).isPresent()
                .hasValueSatisfying(u -> assertThat(u).isEqualTo(user));
    }

    @Test
    void itShouldFindByEmail() {
        // given
        User user = new User();
        user.setUsername("xdavide9");
        user.setPassword("password");
        user.setEmail("valid@email.com");
        // when
        underTest.save(user);
        // then
        assertThat(underTest.findByEmail("valid@email.com")).isPresent()
                .hasValueSatisfying(u -> assertThat(u).isEqualTo(user));
    }

     @Test
     void itShouldFindByUsernameOrEmail() {
         // given
         String username = "username";
         String email = "valid@email.com";
         User user = new User();
         user.setUsername(username);
         user.setEmail("differentValidEmail@email.com");
         user.setPassword("password");
         underTest.save(user);
         // when & then
         assertThat(underTest.existsByEmail("differentValidEmail@email.com")).isTrue();
         assertThat(underTest.existsByUsername(username)).isTrue();
         assertThat(underTest.findByUsernameOrEmail(username, email))
                 .isPresent()
                 .hasValueSatisfying(u -> {
                     assertThat(u.getUsername()).isEqualTo(username);
                     assertThat(u.getEmail()).isEqualTo("differentValidEmail@email.com");
                     assertThat(u.getPassword()).isEqualTo("password");
                 });
     }
 }