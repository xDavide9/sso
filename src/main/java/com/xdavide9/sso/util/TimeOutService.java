package com.xdavide9.sso.util;

import com.xdavide9.sso.properties.TimeOutProperties;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class holds functionality to time out users in the system. This is achieved via the use
 * of concurrent programming. There are 3 methods, each with different parameters depending on specific needs.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class TimeOutService {

    private final TimeOutProperties timeOutProperties;
    private final ScheduledExecutorService scheduler;
    private final UserRepository repository;

    @Autowired
    public TimeOutService(TimeOutProperties timeoutproperties,
                          @Qualifier("timeoutSchedulerExecutorService") ScheduledExecutorService scheduler,
                          UserRepository repository) {
        this.timeOutProperties = timeoutproperties;
        this.scheduler = scheduler;
        this.repository = repository;
    }

    public void timeOut(User user, long duration, TimeUnit timeUnit) {
        user.setEnabled(false);
        repository.save(user);
        scheduler.schedule(() -> {
            user.setEnabled(true);
            repository.save(user);
        }, duration, timeUnit);
    }

    public void timeOut(User user, long duration) {
        timeOut(user, duration, TimeUnit.MILLISECONDS);
    }

    public void timeOut(User user) {
        timeOut(user, timeOutProperties.getDefaultTimeOutDuration());
    }

    @PreDestroy
    public void shutDown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS))
                scheduler.shutdownNow();
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
