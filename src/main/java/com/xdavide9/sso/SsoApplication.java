package com.xdavide9.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

/**
 * Main class of {@link SsoApplication}.
 * Currently run with dev spring profile as it is under development
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@SpringBootApplication
public class SsoApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SsoApplication.class);
        app.run(args);
    }
}
