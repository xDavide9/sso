package com.xdavide9.sso.user.change;

import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.user.fields.UserField;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class UserChangeRepositoryTest {

    @Autowired
    private UserChangeRepository underTest;
    @Autowired
    private UserRepository userRepository;

    @Test
    void itShouldSaveChanges() {
        // given
        User user = new User(
                "username",
                "email@email.com",
                "VeryStrongPass1!"
        );
        userRepository.save(user);
        UserChange change = new UserChange(
                user,
                UserField.USERNAME,
                "username",
                "username2"
        );
        change.setCreationDate(LocalDateTime.now());
        change.setCreatedBy(UUID.randomUUID());
        // when
        underTest.save(change);
        // then
        Optional<UserChange> byId = underTest.findById(1L);
        assertThat(byId)
                .isPresent()
                .hasValueSatisfying(userChange -> assertThat(userChange).isEqualTo(change));
    }
}
