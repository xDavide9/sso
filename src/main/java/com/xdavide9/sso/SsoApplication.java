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
        app.setDefaultProperties(Collections
                .singletonMap("spring.profiles.default", "dev"));
        app.run(args);
    }

    // TODO look further into native and nativeTest profiles
}
