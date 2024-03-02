package com.xdavide9.sso.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

/**
 * This class models properties with prefix "api.timeout" in properties files
 * to be used across the application. They are set via constructor with default configuration
 * but can later be modified by other beans (e.g. command line runners in testing env)
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@ConfigurationProperties(prefix = "api.timeout")
public class TimeOutProperties {
    /**
     * the amount of threads allocated
     */
    private int threads;
    /**
     * default timeout duration in milliseconds
     */
    private long defaultDuration;

    @ConstructorBinding
    public TimeOutProperties(int threads,
                             long defaultDuration) {
        this.threads = threads;
        this.defaultDuration = defaultDuration;
    }

    // GETTERS

    public int getNumberOfThreads() {
        return threads;
    }

    public long getDefaultTimeOutDuration() {
        return defaultDuration;
    }

    // SETTERS

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setDefaultDuration(long defaultDuration) {
        this.defaultDuration = defaultDuration;
    }
}
