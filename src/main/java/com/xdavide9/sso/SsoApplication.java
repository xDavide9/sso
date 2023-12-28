package com.xdavide9.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Main class of {@link SsoApplication}.
 * There are different Spring profiles for each purpose:
 * prod, dev, test. These profiles can be activated by homonym
 * maven profiles or manually when starting the application.
 * Property values are handled in com.xdavide9.sso.properties and can be used
 * across the application via different beans depending on the properties themselves.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@SpringBootApplication
@ConfigurationPropertiesScan("com.xdavide9.sso.properties")
public class SsoApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SsoApplication.class);
        app.run(args);
    }
}
