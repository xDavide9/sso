package com.xdavide9.sso.common.config;

import com.xdavide9.sso.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

// Modified the default expiration of a jwt in a testing environment where importee

@TestConfiguration
public class OverrideJwtPropertiesConfig {

    @Autowired
    private JwtProperties jwtProperties;

    @Bean
    public CommandLineRunner overriddenJwtProperties() {
        return args -> jwtProperties.setExpiration(1000);
    }
}
