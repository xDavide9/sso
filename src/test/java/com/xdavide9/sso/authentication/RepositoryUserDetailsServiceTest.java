package com.xdavide9.sso.authentication;

import com.xdavide9.sso.exception.authentication.UsernameNorEmailNotFoundException;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RepositoryUserDetailsServiceTest {

    @InjectMocks
    private RepositoryUserDetailsService underTest;

    @Mock
    private UserRepository repository;

    @Test
    void itShouldLoadByUsernameOrEmailCorrectly() {
        // given
        User user = new User();
        user.setUsername("username");
        user.setEmail("valid@email.com");
        user.setPassword("password");
        String userInput = "username";
        given(repository.findByUsernameOrEmail(userInput, userInput))
                .willReturn(Optional.of(user));
        // when
        User returnedUser = (User) underTest.loadUserByUsername(userInput);
        // then
        assertThat(returnedUser).isEqualTo(user);
    }

    @Test
    void itShouldNotLoadByUsernameOrEmailAndThrow() {
        // given
        User user = new User();
        user.setUsername("username");
        user.setEmail("valid@email.com");
        user.setPassword("password");
        String input = "username";
        given(repository.findByUsernameOrEmail(input, input))
                .willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> underTest.loadUserByUsername(input))
                .isInstanceOf(UsernameNorEmailNotFoundException.class)
                .hasMessageContaining(format("User with username or email [%s] not found.", input));
    }
}