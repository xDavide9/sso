package com.xdavide9.sso.util;

import com.xdavide9.sso.properties.TimeOutProperties;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TimeOutServiceTest {

    private TimeOutService underTest;

    @Mock
    private TimeOutProperties timeOutProperties;

    @Mock
    private UserRepository repository;

    @Captor
    private ArgumentCaptor<User> captor;

    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        underTest = new TimeOutService(timeOutProperties, repository, clock);
    }

    @Test
    void itShouldTimeOutWithGivenTemporalUnitAndDefaultDuration() {
        // given
        User user = new User();
        // when
        underTest.timeOut(user, 1, ChronoUnit.HOURS);
        // then
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isEnabled()).isFalse();
        assertThat(captor.getValue().getDisabledUntil()).isEqualTo(LocalDateTime.now(clock).plusHours(1));
    }

    @Test
    void itShouldTimeOutWithDefaultTemporalUnitAndGivenDuration() {
        // given
        User user = new User();
        given(timeOutProperties.getDefaultTemporalUnit()).willReturn("HOURS");
        // when
        underTest.timeOut(user, 1);
        // then
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isEnabled()).isFalse();
        assertThat(captor.getValue().getDisabledUntil()).isEqualTo(LocalDateTime.now(clock).plusHours(1));
    }

    @Test
    void itShouldTimeOutWithDefaultTemporalUnitAndDefaultDuration() {
        // given
        User user = new User();
        given(timeOutProperties.getDefaultTemporalUnit()).willReturn("HOURS");
        given(timeOutProperties.getDefaultTimeOutDuration()).willReturn(1L);
        // when
        underTest.timeOut(user);
        // then
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isEnabled()).isFalse();
        assertThat(captor.getValue().getDisabledUntil()).isEqualTo(LocalDateTime.now(clock).plusHours(1));
    }
}