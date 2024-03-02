package com.xdavide9.sso.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

/**
 * This class models properties with prefix "jwt" in properties files
 * to be used across the application.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * secret key that is used to sign jwt tokens
     */
    private String secretKey;
    /**
     * defines how long until a jwt token expires in milliseconds
     */
    private long expiration;
    @ConstructorBinding
    public JwtProperties(String secretKey,
                         long expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
    }

    // GETTERS

    public String getSecretKey() {
        return secretKey;
    }
    public long getExpiration() {
        return expiration;
    }

    // SETTERS

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}
