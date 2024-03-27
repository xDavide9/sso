package com.xdavide9.sso.util;

import com.xdavide9.sso.properties.TimeOutProperties;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * This class holds functionality to time out user. It does so by setting boolean field "enabled" to false
 * and setting a "disabledUntil" timestamp that can be checked to see if they are still disabled.
 * {@link TimeOutProperties} holds default temporal unit and default timeout duration.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class TimeOutService {

    private final TimeOutProperties timeOutProperties;
    private final UserRepository repository;
    private final Clock clock;

    @Autowired
    public TimeOutService(TimeOutProperties timeoutproperties,
                          UserRepository repository,
                          Clock clock) {
        this.timeOutProperties = timeoutproperties;
        this.repository = repository;
        this.clock = clock;
    }

    /**
     * Times out user for specified duration and time unit.
     */
    @Transactional
    public synchronized void timeOut(User user, long duration, TemporalUnit temporalUnit) {
        user.setEnabled(false);
        user.setDisabledUntil(LocalDateTime.now(clock).plus(duration, temporalUnit));
        repository.save(user);
    }

    /**
     * Times out user for specified duration in default temporal unit defined by {@link TimeOutProperties} (see application.properties)
     */
    @Transactional
    public synchronized void timeOut(User user, long duration) {
        timeOut(user, duration, ChronoUnit.valueOf(timeOutProperties.getDefaultTemporalUnit()));
    }

    /**
     * Times out user for default duration defined by {@link TimeOutProperties} (see application.properties)
     */
    @Transactional
    public synchronized void timeOut(User user) {
        timeOut(user, timeOutProperties.getDefaultTimeOutDuration());
    }
}
