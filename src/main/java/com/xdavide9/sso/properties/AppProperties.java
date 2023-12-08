package com.xdavide9.sso.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
/**
 * This class models properties with prefix "app" in application-dev.properties to be used across the application.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name;
    private String version;
    private String description;
    private String groupId;
    private String jdk;

    // GETTERS

    public String getName() {
        return name;
    }
    public String getVersion() {
        return version;
    }
    public String getDescription() {
        return description;
    }
    public String getGroupId() {
        return groupId;
    }
    public String getJdk() {
        return jdk;
    }

    // SETTERS

    /**
     * This setter is used by Spring to inject the property value.
     * It should not be called anywhere else.
     * @since 0.0.1-SNAPSHOT
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This setter is used by Spring to inject the property value.
     * It should not be called anywhere else.
     * @since 0.0.1-SNAPSHOT
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * This setter is used by Spring to inject the property value.
     * It should not be called anywhere else.
     * @since 0.0.1-SNAPSHOT
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This setter is used by Spring to inject the property value.
     * It should not be called anywhere else.
     * @since 0.0.1-SNAPSHOT
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * This setter is used by Spring to inject the property value.
     * It should not be called anywhere else.
     * @since 0.0.1-SNAPSHOT
     */
    public void setJdk(String jdk) {
        this.jdk = jdk;
    }
}
