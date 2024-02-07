package com.xdavide9.sso.util;

import com.xdavide9.sso.properties.TimeOutProperties;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TimeOutServiceTest {

    @InjectMocks
    private TimeOutService underTest;

    @Mock
    private TimeOutProperties timeOutProperties;

    @Mock
    private ScheduledExecutorService scheduledExecutorService;

    @Mock
    private UserRepository repository;

    @Captor
    private ArgumentCaptor<Runnable> captor;

    @Test
    void itShouldTimeoutCorrectlyWithoutAnyArgs() {
        // given
        User user = new User();
        given(timeOutProperties.getDefaultTimeOutDuration()).willReturn(1000L);
        // when
        underTest.timeOut(user);
        // then
        assertThat(user.isEnabled()).isFalse();
        verify(scheduledExecutorService).schedule(captor.capture(), eq(1000L), eq(TimeUnit.MILLISECONDS));
        captor.getValue().run();
        assertThat(user.isEnabled()).isTrue();
        verify(repository, times(2)).save(user);
    }

    @Test
    void itShouldTimeoutCorrectlyWithGivenDuration() {
        // given
        User user = new User();
        // when
        underTest.timeOut(user, 500L);  // in milliseconds
        // then
        assertThat(user.isEnabled()).isFalse();
        verify(scheduledExecutorService).schedule(captor.capture(), eq(500L), eq(TimeUnit.MILLISECONDS));
        captor.getValue().run();
        assertThat(user.isEnabled()).isTrue();
        verify(repository, times(2)).save(user);
    }

    @Test
    void itShouldTimeoutCorrectlyWithGivenDurationAndTimeUnit() {
        // given
        User user = new User();
        // when
        underTest.timeOut(user, 1L, TimeUnit.MINUTES);
        // then
        assertThat(user.isEnabled()).isFalse();
        verify(scheduledExecutorService).schedule(captor.capture(), eq(1L), eq(TimeUnit.MINUTES));
        captor.getValue().run();
        assertThat(user.isEnabled()).isTrue();
        verify(repository, times(2)).save(user);
    }
}