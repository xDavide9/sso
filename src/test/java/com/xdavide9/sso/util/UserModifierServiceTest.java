package com.xdavide9.sso.util;

import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserModifierServiceTest {

    @InjectMocks
    private UserModifierService underTest;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<User> captor;

    @Test
    void itShouldSetAttributeCorrectly() {
        // given
        User user = new User();
        String username = "username";
        // when
        underTest.setAttribute(user, username, User::setUsername);
        // then
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo(username);
    }
}