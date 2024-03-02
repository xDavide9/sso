package com.xdavide9.sso.common.config;

import com.xdavide9.sso.properties.TimeOutProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

// Modifies the default duration of a timeout in a testing environment where is imported

@TestConfiguration
public class OverrideTimeOutPropertiesConfig {

    @Autowired
    private TimeOutProperties timeOutProperties;

    @Bean
    public CommandLineRunner overriddenTimeOutProperties() {
        return args -> timeOutProperties.setDefaultDuration(1000);
    }
}
