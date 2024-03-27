package com.xdavide9.sso.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.temporal.ChronoUnit;

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
     * temporal unit to timeout in (hours, minutes, seconds...);
     * should be the string representation of an enum constant of {@link ChronoUnit}
     */
    private String defaultTemporalUnit;
    /**
     * default timeout duration expressed in temporal units
     */
    private long defaultTimeOutDuration;

    @ConstructorBinding
    public TimeOutProperties(String defaultTemporalUnit,
                             long defaultTimeOutDuration) {
        this.defaultTemporalUnit = defaultTemporalUnit;
        this.defaultTimeOutDuration = defaultTimeOutDuration;
    }

    // GETTERS


    public String getDefaultTemporalUnit() {
        return defaultTemporalUnit;
    }

    public long getDefaultTimeOutDuration() {
        return defaultTimeOutDuration;
    }

    // SETTERS


    public void setDefaultTemporalUnit(String defaultTemporalUnit) {
        this.defaultTemporalUnit = defaultTemporalUnit;
    }

    public void setDefaultTimeOutDuration(long defaultDuration) {
        this.defaultTimeOutDuration = defaultDuration;
    }
}
