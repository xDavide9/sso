package com.xdavide9.sso.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

/**
 * This class models properties with prefix "api.timeout" in properties files
 * to be used across the application.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@ConfigurationProperties(prefix = "api.timeout")
public class TimeOutProperties {
    /**
     * the amount of threads allocated
     */
    private final int threads;
    /**
     * default timeout duration in milliseconds
     */
    private final long defaultDuration;

    @ConstructorBinding
    public TimeOutProperties(int threads,
                             long defaultDuration) {
        this.threads = threads;
        this.defaultDuration = defaultDuration;
    }

    public int getNumberOfThreads() {
        return threads;
    }

    public long getDefaultTimeOutDuration() {
        return defaultDuration;
    }
}
