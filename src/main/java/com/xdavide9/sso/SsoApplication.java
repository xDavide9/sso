package com.xdavide9.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * main class
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@SpringBootApplication
public class SsoApplication {
    /**
     * main method
     * @param args args
     * @since 0.0.1-SNAPSHOT
     */
    public static void main(String[] args) {
        SpringApplication.run(SsoApplication.class, args);
    }

    /**
     * empty constructor
     * @since 0.0.1-SNAPSHOT
     */
    public SsoApplication() {}
}
