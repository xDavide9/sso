package com.xdavide9.sso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * This class configures beans related to the management of time in the application
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
@Configuration
public class TimeConfig {

    @Bean
    public Clock defaultClock() {
        return Clock.systemDefaultZone();
    }
}
