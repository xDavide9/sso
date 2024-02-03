package com.xdavide9.sso.authentication;

import com.xdavide9.sso.exception.authentication.SubjectNotFoundException;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

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
        String subject = "username";
        given(repository.findByUsernameOrEmail(subject, subject))
                .willReturn(Optional.of(user));
        // when
        User returnedUser = (User) underTest.loadUserByUsername(subject);
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
        String subject = "username";
        given(repository.findByUsernameOrEmail(subject, subject))
                .willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> underTest.loadUserByUsername(subject))
                .isInstanceOf(SubjectNotFoundException.class)
                .hasMessageContaining(format("User with subject [%s] not found.", subject));
    }
}