package com.xdavide9.sso.user.change;

import com.xdavide9.sso.exception.user.change.UserChangeNotFoundException;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.api.OperatorService;
import com.xdavide9.sso.user.fields.UserField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserChangeServiceTest {

    @InjectMocks
    private UserChangeService underTest;
    @Mock
    private UserChangeRepository userChangeRepository;
    @Mock
    private OperatorService operatorService;

    @Test
    void itShouldGetAllChangesCorrectly() {
        // given
        // when
        underTest.getAllChanges();
        // then
        verify(userChangeRepository).findAll();
    }

    @Test
    void itShouldGetChangeCorrectly() {
        // given
        Long id = 1L;
        UserChange userChange = new UserChange(
                new User(), UserField.USERNAME, "previous", "updated"
        );
        given(userChangeRepository.findById(id))
                .willReturn(Optional.of(userChange));
        // when
        UserChange change = underTest.getChange(id);
        // then
        assertThat(userChange).isEqualTo(change);
    }

    @Test
    void itShouldNotGetChangeNotFound() {
        // given
        Long id = 1L;
        // when & then
        assertThatThrownBy(() -> underTest.getChange(id))
                .isInstanceOf(UserChangeNotFoundException.class)
                .hasMessageContaining(format("A specific change with id [%s] made to a user was not found", id));
    }

    @Test
    void itShouldGetChangesPerUser() {
        // given
        UUID uuid = UUID.randomUUID();
        // when
        underTest.getChangesPerUser(uuid);
        // then
        verify(userChangeRepository).findAllByUserIs(operatorService.getUserByUuid(uuid));
    }
}