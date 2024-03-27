package com.xdavide9.sso.common.config;

import com.xdavide9.sso.properties.TimeOutProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.temporal.ChronoUnit;

// Modifies the default duration and temporal unit of a timeout in a testing environment where is imported

@TestConfiguration
public class OverrideTimeOutPropertiesConfig {

    @Autowired
    private TimeOutProperties timeOutProperties;

    @Bean
    public CommandLineRunner overrideTimeOutProperties() {
        return args ->  {
            timeOutProperties.setDefaultTimeOutDuration(1000);
            timeOutProperties.setDefaultTemporalUnit("MILLIS");
        };
    }
}
