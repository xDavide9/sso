package com.xdavide9.sso.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class models properties with prefix "jwt" in application.properties to be used across the application.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * secret key that is used to sign jwt tokens
     * @since 0.0.1-SNAPSHOT
     */
    private String secretKey;

    /**
     * defines how long until a jwt token expires
     * @since 0.0.1-SNAPSHOT
     */
    private long expiration;

    /**
     * getter
     * @return secret key
     * @since 0.0.1-SNAPSHOT
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * getter
     * @return expiration
     * @since 0.0.1-SNAPSHOT
     */
    public long getExpiration() {
        return expiration;
    }

    /**
     * This setter is used by Spring to inject the property value.
     * It should not be called anywhere else.
     * @since 0.0.1-SNAPSHOT
     * @param secretKey secretKey
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * This setter is used by Spring to inject the property value.
     * It should not be called anywhere else.
     * @since 0.0.1-SNAPSHOT
     * @param expiration expiration
     */
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

}
