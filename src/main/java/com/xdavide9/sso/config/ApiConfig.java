package com.xdavide9.sso.config;

import com.xdavide9.sso.properties.TimeOutProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This class configures beans related to the user api.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
@Configuration
public class ApiConfig {

    private final TimeOutProperties timeOutProperties;

    @Autowired
    public ApiConfig(TimeOutProperties timeOutProperties) {
        this.timeOutProperties = timeOutProperties;
    }

    @Bean
    ScheduledExecutorService timeoutSchedulerExecutorService() {
        return Executors.newScheduledThreadPool(timeOutProperties.getNumberOfThreads());
    }
}
